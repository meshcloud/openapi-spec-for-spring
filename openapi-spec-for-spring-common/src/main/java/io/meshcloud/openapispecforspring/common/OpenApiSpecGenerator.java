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

package io.meshcloud.openapispecforspring.common;

import io.meshcloud.openapispecforspring.common.paths.PathsBuilder;
import io.meshcloud.openapispecforspring.common.reference.ReferencedItemSupport;
import io.meshcloud.openapispecforspring.common.reference.ReferencedItemSupportFactory;
import io.meshcloud.openapispecforspring.model.OpenApi;
import io.meshcloud.openapispecforspring.model.path.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OpenApiSpecGenerator {

    private final PathsBuilder pathsBuilder;
    private final ReferencedItemSupportFactory referencedItemSupportFactory;
    private final List<OpenApiCustomizer> openApiCustomizers;

    public OpenApi generateOpenApi() {

        ReferencedItemSupport referencedItemSupport = referencedItemSupportFactory.create();
        Paths paths = pathsBuilder.buildPaths(referencedItemSupport.getReferencedItemConsumerSupplier());
        OpenApi openApi = new OpenApi();
        openApi.setPaths(paths); // always set paths, even if empty to comply with spec
        referencedItemSupport.applyToOpenApi(openApi);
        openApiCustomizers.forEach(customizer -> customizer.customize(openApi));
        return openApi;
    }
}
