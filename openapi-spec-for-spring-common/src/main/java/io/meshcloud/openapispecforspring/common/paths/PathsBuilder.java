/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
 * %%
 * Copyright (C) 2020-2023 QAware GmbH
 * Copyright (C) 2026 meshcloud GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package io.meshcloud.openapispecforspring.common.paths;

import static io.meshcloud.openapispecforspring.common.util.OpenApiCollectionUtils.firstNonNull;

import io.meshcloud.openapispecforspring.common.filter.handlermethod.HandlerMethodFilter;
import io.meshcloud.openapispecforspring.common.filter.pathitem.PathItemFilter;
import io.meshcloud.openapispecforspring.common.operation.OperationWithInfo;
import io.meshcloud.openapispecforspring.common.operation.id.OperationIdConflictResolver;
import io.meshcloud.openapispecforspring.common.reference.ReferencedItemConsumerSupplier;
import io.meshcloud.openapispecforspring.common.util.OpenApiStreamUtils;
import io.meshcloud.openapispecforspring.model.operation.Operation;
import io.meshcloud.openapispecforspring.model.path.PathItem;
import io.meshcloud.openapispecforspring.model.path.Paths;
import io.meshcloud.openapispecforspring.model.path.RequestMethod;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequiredArgsConstructor
public class PathsBuilder {

    private final HandlerMethodsProvider handlerMethodsProvider;
    private final List<HandlerMethod.Merger> handlerMethodMergers;
    private final PathItemBuilderFactory pathItemBuilderFactory;
    private final List<PathItemFilter> pathItemFilters;
    private final List<HandlerMethodFilter> handlerMethodFilters;
    private final OperationIdConflictResolver operationIdConflictResolver;

    public Paths buildPaths(ReferencedItemConsumerSupplier referencedItemConsumerSupplier) {
        MultiValueMap<String, OperationWithInfo> operationsById = new LinkedMultiValueMap<>();
        Paths paths = new Paths();
        handlerMethodsProvider.getHandlerMethods().stream()
                .flatMap(handlerMethodWithInfo -> handlerMethodWithInfo.getPathPatterns().stream()
                        .map(pathPattern -> StringUtils.isBlank(pathPattern) ? "/" : pathPattern)
                        .map(pathPattern -> Pair.of(pathPattern, handlerMethodWithInfo)))
                .filter(item -> isAcceptedByAllHandlerMethodFilters(item.getRight()))
                .sorted(Comparator.comparing(Pair::getLeft))
                // there might be more than one handlerWithInfo with the same pathPattern,
                // so we need to re-group this here
                .collect(Collectors.groupingBy(
                        Pair::getLeft, LinkedHashMap::new, Collectors.mapping(Pair::getRight, Collectors.toList())))
                .forEach((pathPattern, handlerMethodWithInfos) -> {
                    Map<RequestMethod, HandlerMethod> mergedHandlerMethods = handlerMethodWithInfos.stream()
                            .flatMap(handlerMethodWithInfo -> handlerMethodWithInfo.getRequestMethods().stream()
                                    .map(requestMethod ->
                                            Pair.of(requestMethod, handlerMethodWithInfo.getHandlerMethod())))
                            .sorted(Map.Entry.comparingByKey()) // natural order of the enum RequestMethod
                            .collect(OpenApiStreamUtils.groupingByPairKeyAndCollectingValuesToList())
                            .entrySet()
                            .stream()
                            .map(entry -> {
                                List<HandlerMethod> unmergedHandlerMethods = entry.getValue();
                                if (unmergedHandlerMethods.size() == 1) {
                                    return Pair.of(entry.getKey(), unmergedHandlerMethods.get(0));
                                }
                                return firstNonNull(
                                                handlerMethodMergers, merger -> merger.merge(unmergedHandlerMethods))
                                        .map(mergedHandlerMethod -> Pair.of(entry.getKey(), mergedHandlerMethod))
                                        .orElseThrow(() -> new IllegalStateException("Cannot merge handler methods for "
                                                + entry.getKey() + " " + pathPattern + ": " + unmergedHandlerMethods));
                            })
                            .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (a, b) -> b, LinkedHashMap::new));

                    PathItemBuilderFactory.PathItemBuilder pathItemBuilder =
                            pathItemBuilderFactory.create(referencedItemConsumerSupplier);
                    PathItem pathItem = pathItemBuilder.build(pathPattern, mergedHandlerMethods);
                    if (isAcceptedByAllPathItemFilters(pathItem, pathPattern)) {
                        operationsById.addAll(pathItemBuilder.getOperationsById());
                        paths.put(pathPattern, pathItem);
                    }
                });
        resolveOperationIdConflicts(operationsById);
        return paths;
    }

    private void resolveOperationIdConflicts(MultiValueMap<String, OperationWithInfo> operationsById) {
        operationsById.forEach((operationId, operationsWithInfo) -> {
            // more than one operation with same id is a conflict
            if (operationsWithInfo.size() > 1) {
                // more than one operation per ID is a conflict which should be resolved!
                operationIdConflictResolver.resolveConflict(operationId, operationsWithInfo);
                checkIfOperationIdConflictIsResolved(operationsById, operationId, operationsWithInfo);
            }
        });
    }

    private void checkIfOperationIdConflictIsResolved(
            MultiValueMap<String, OperationWithInfo> operationsById,
            String operationId,
            List<OperationWithInfo> operationsWithInfo) {
        Set<String> uniqueOperationIds = operationsWithInfo.stream()
                .map(OperationWithInfo::getOperation)
                .map(Operation::getOperationId)
                .collect(Collectors.toSet());
        if (uniqueOperationIds.size() != operationsWithInfo.size()) {
            throw new IllegalStateException("The operation id conflict resolution for " + operationId
                    + " did not generate unique IDs but " + uniqueOperationIds);
        }
        uniqueOperationIds.stream()
                .filter(uniqueOperationId -> !uniqueOperationId.equals(operationId))
                .forEach(uniqueOperationId -> {
                    if (operationsById.containsKey(uniqueOperationId)) {
                        throw new IllegalStateException("The operation id conflict resolution for " + operationId
                                + " generated an already used unique id: " + uniqueOperationId);
                    }
                });
    }

    private boolean isAcceptedByAllHandlerMethodFilters(HandlerMethodWithInfo handlerMethod) {
        return handlerMethodFilters.stream().allMatch(handlerMethodFilter -> handlerMethodFilter.accept(handlerMethod));
    }

    private boolean isAcceptedByAllPathItemFilters(PathItem pathItem, String pathPattern) {
        return pathItemFilters.stream().allMatch(pathItemFilter -> pathItemFilter.accept(pathItem, pathPattern));
    }
}
