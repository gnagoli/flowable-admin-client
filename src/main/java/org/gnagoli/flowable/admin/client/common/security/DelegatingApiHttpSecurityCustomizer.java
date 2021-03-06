/* Licensed under the Apache License, Version 2.0 (the "License");
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
 */
package org.gnagoli.flowable.admin.client.common.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Collection;

/**
 * @author Filip Hrisafov
 */
public class DelegatingApiHttpSecurityCustomizer implements ApiHttpSecurityCustomizer {

    protected final Collection<ApiHttpSecurityCustomizer> customizers;

    public DelegatingApiHttpSecurityCustomizer(Collection<ApiHttpSecurityCustomizer> customizers) {
        this.customizers = customizers;
    }

    @Override
    public void customize(HttpSecurity http) throws Exception {
        for (ApiHttpSecurityCustomizer customizer : customizers) {
            customizer.customize(http);
        }
    }
}
