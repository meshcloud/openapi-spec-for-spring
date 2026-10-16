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

package io.meshcloud.openapispecforspring.common.paths;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathPatternVariables {

    public static String withoutRegex(String pathPattern) {
        StringBuilder result = new StringBuilder();
        parse(pathPattern, result, new LinkedHashMap<>());
        return result.toString();
    }

    public static Map<String, String> regexByVariable(String pathPattern) {
        Map<String, String> regexByVariable = new LinkedHashMap<>();
        parse(pathPattern, new StringBuilder(), regexByVariable);
        return regexByVariable;
    }

    private static void parse(String pathPattern, StringBuilder withoutRegex, Map<String, String> regexByVariable) {
        int index = 0;
        while (index < pathPattern.length()) {
            char character = pathPattern.charAt(index);
            if (character != '{') {
                withoutRegex.append(character);
                index++;
                continue;
            }
            int end = findClosingBrace(pathPattern, index);
            String variable = pathPattern.substring(index + 1, end);
            int colon = variable.indexOf(':');
            String name = colon < 0 ? variable : variable.substring(0, colon);
            if (colon >= 0) {
                regexByVariable.put(name, variable.substring(colon + 1));
            }
            withoutRegex.append('{').append(name).append('}');
            index = end + 1;
        }
    }

    private static int findClosingBrace(String pathPattern, int openingBrace) {
        int depth = 0;
        int index = openingBrace;
        while (index < pathPattern.length()) {
            char character = pathPattern.charAt(index);
            if (character == '\\') {
                index += 2;
                continue;
            }
            if (character == '{') {
                depth++;
            } else if (character == '}' && --depth == 0) {
                return index;
            }
            index++;
        }
        throw new IllegalArgumentException("Unbalanced braces in path pattern " + pathPattern);
    }
}
