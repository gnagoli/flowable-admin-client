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
package org.gnagoli.flowable.admin.client.admin.rest.client;

import org.gnagoli.flowable.admin.client.admin.logic.domain.EndpointType;
import org.gnagoli.flowable.admin.client.admin.logic.domain.ServerConfig;
import org.gnagoli.flowable.admin.client.admin.logic.repository.ServerConfigRepository;
import org.gnagoli.flowable.admin.client.common.service.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractClientResource {

    private static final String SERVER_ID = "serverId";

    @Autowired
    protected ServerConfigRepository configRepository;

    protected ServerConfig retrieveServerConfig(EndpointType endpointType) {
        List<ServerConfig> serverConfigs = configRepository.getByEndpointType(endpointType);

        if (serverConfigs == null) {
            throw new BadRequestException("No server config found");
        }

        if (serverConfigs.size() > 1) {
            throw new BadRequestException("Only one server config per endpoint type allowed");
        }

        return serverConfigs.get(0);
    }

    protected Map<String, String[]> getRequestParametersWithoutServerId(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String[]> resultMap = new HashMap<>(parameterMap);
        resultMap.remove(SERVER_ID);
        return resultMap;
    }

}
