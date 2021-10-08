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

import com.fasterxml.jackson.databind.JsonNode;
import org.gnagoli.flowable.admin.client.admin.logic.domain.EndpointType;
import org.gnagoli.flowable.admin.client.admin.logic.domain.ServerConfig;
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.DecisionHistoricExecutionService;
import org.gnagoli.flowable.admin.client.common.service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Yvo Swillens
 */
@RestController
public class DecisionTableHistoricExecutionsClientResource extends AbstractClientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecisionTableHistoricExecutionsClientResource.class);

    @Autowired
    protected DecisionHistoricExecutionService clientService;

    @GetMapping(value = "/rest/admin/decision-tables/history", produces = "application/json")
    public JsonNode getDecisionTableHistoricExecution(HttpServletRequest request) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.DMN);
        Map<String, String[]> parameterMap = getRequestParametersWithoutServerId(request);
        return clientService.listHistoricDecisionExecutions(serverConfig, parameterMap);
    }
}
