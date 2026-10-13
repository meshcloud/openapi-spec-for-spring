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

package io.meshcloud.openapispecforspring.common.mapper;

import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.buildStringMapFromStream;

import io.swagger.v3.oas.annotations.links.LinkParameter;
import java.util.Arrays;
import java.util.Map;

public class DefaultLinkParameterAnnotationMapper implements LinkParameterAnnotationMapper {

    @Override
    public Map<String, String> mapArray(LinkParameter[] linkParameterAnnotations) {
        return buildStringMapFromStream(Arrays.stream(linkParameterAnnotations), LinkParameter::name, this::map);
    }

    @Override
    public String map(LinkParameter linkAnnotation) {
        return linkAnnotation.expression();
    }
}
