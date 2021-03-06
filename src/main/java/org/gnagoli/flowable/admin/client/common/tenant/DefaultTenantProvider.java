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
package org.gnagoli.flowable.admin.client.common.tenant;

import org.apache.commons.lang3.StringUtils;
import org.gnagoli.flowable.admin.client.common.properties.FlowableCommonAppProperties;
import org.gnagoli.flowable.admin.client.common.security.SecurityScope;
import org.gnagoli.flowable.admin.client.common.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultTenantProvider implements TenantProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTenantProvider.class);

    private String tenantId;
    
    public DefaultTenantProvider(FlowableCommonAppProperties commonAppProperties) {
        super();
        String configuredTenantId = commonAppProperties.getTenantId();
        if (!StringUtils.isBlank(configuredTenantId)) {
            // trim whitespace as trailing whitespace are possible in properties files and easy to do
            configuredTenantId = configuredTenantId.trim();

            // quotes can help solve whitespace issues
            LOGGER.debug("Found configured tenantId: '{}'", configuredTenantId);

            this.tenantId = configuredTenantId;
        }
    }

    @Override
    public String getTenantId() {
        if (tenantId != null) {
            LOGGER.debug("Using configured tenantId: '{}'", tenantId);
            return tenantId;
        }

        SecurityScope currentSecurityScope = SecurityUtils.getCurrentSecurityScope();
        if (currentSecurityScope != null) {
            String tenantId = currentSecurityScope.getTenantId();
            // quotes can help solve whitespace issues, trimming here would not 
            // help solve the problem at source which is in user database
            LOGGER.debug("Using user tenantId: '{}'", tenantId);

            return tenantId;
        }

        LOGGER.debug("No tenantId");

        return null;
    }
    
}
