/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
 * %%
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

package io.meshcloud.openapispecforspring.common.operation.parameter.customizer;

import static io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils.laterThan;

import io.meshcloud.openapispecforspring.common.paths.PathPatternVariables;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

public class DefaultOperationParameterPathPatternCustomizer implements OperationParameterCustomizer {

    // The schema of the parameter must exist already.
    public static final int ORDER = laterThan(DEFAULT_ORDER);

    @Override
    public void customize(Parameter parameter, OperationParameterCustomizerContext context) {
        Schema schema = parameter.getSchema();
        if (!ParameterIn.PATH.toString().equals(parameter.getIn()) || schema == null || schema.getRef() != null) {
            return;
        }
        String regex = PathPatternVariables.regexByVariable(
                        context.getOperationInfo().getPathPattern())
                .get(parameter.getName());
        if (regex != null) {
            schema.setPattern(regex);
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
