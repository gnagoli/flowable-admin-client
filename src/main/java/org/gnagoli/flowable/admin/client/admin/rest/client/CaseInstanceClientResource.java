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
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gnagoli.flowable.admin.client.admin.logic.domain.EndpointType;
import org.gnagoli.flowable.admin.client.admin.logic.domain.ServerConfig;
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.CaseInstanceService;
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.exception.FlowableServiceException;
import org.gnagoli.flowable.admin.client.common.service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class CaseInstanceClientResource extends AbstractClientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaseInstanceClientResource.class);

    @Autowired
    protected CaseInstanceService clientService;

    @GetMapping(value = "/rest/admin/case-instances/{caseInstanceId}", produces = "application/json")
    public JsonNode getProcessInstance(@PathVariable String caseInstanceId, @RequestParam(required = false, defaultValue = "false") boolean runtime) throws BadRequestException {

        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            return clientService.getCaseInstance(serverConfig, caseInstanceId);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting case instance {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(value = "/rest/admin/case-instances/{caseInstanceId}/tasks")
    public JsonNode getSubtasks(@PathVariable String caseInstanceId) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            return clientService.getTasks(serverConfig, caseInstanceId);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting tasks for case instance {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(value = "/rest/admin/case-instances/{caseInstanceId}/variables")
    public JsonNode getVariables(@PathVariable String caseInstanceId) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            return clientService.getVariables(serverConfig, caseInstanceId);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting variables for case instance {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping(value = "/rest/admin/case-instances/{caseInstanceId}/variables/{variableName}")
    @ResponseStatus(value = HttpStatus.OK)
    public void updateVariable(@PathVariable String caseInstanceId, @PathVariable String variableName, @RequestBody ObjectNode body) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            clientService.updateVariable(serverConfig, caseInstanceId, variableName, body);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error updating variable {} for case instance {}", variableName, caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping(value = "/rest/admin/case-instances/{caseInstanceId}/variables")
    @ResponseStatus(value = HttpStatus.OK)
    public void createVariable(@PathVariable String caseInstanceId, @RequestBody ObjectNode body) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            clientService.createVariable(serverConfig, caseInstanceId, body);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error creating variable for case instance {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping(value = "/rest/admin/case-instances/{caseInstanceId}/variables/{variableName}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteVariable(@PathVariable String caseInstanceId, @PathVariable String variableName) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            clientService.deleteVariable(serverConfig, caseInstanceId, variableName);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error deleting variable for case instance {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(value = "/rest/admin/case-instances/{caseInstanceId}/jobs")
    public JsonNode getJobs(@PathVariable String caseInstanceId) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            return clientService.getJobs(serverConfig, caseInstanceId);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting jobs for case instance {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping(value = "/rest/admin/case-instances/{caseInstanceId}")
    @ResponseStatus(value = HttpStatus.OK)
    public void executeAction(@PathVariable String caseInstanceId, @RequestBody JsonNode actionBody) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            clientService.executeAction(serverConfig, caseInstanceId, actionBody);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error executing action on case instance {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(value = "/rest/admin/case-instances/{caseInstanceId}/decision-executions")
    public JsonNode getDecisionExecutions(@PathVariable String caseInstanceId) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.DMN);
        try {
            return clientService.getDecisionExecutions(serverConfig, caseInstanceId);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting decision executions {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }
    
    @PostMapping(value = "/rest/admin/case-instances/{caseInstanceId}/change-state")
    @ResponseStatus(value = HttpStatus.OK)
    public void changePlanItemState(@PathVariable String caseInstanceId, @RequestBody JsonNode changeStateBody) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            clientService.changePlanItemState(serverConfig, caseInstanceId, changeStateBody);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error changing plan item state for case instance {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }
    
    @PostMapping(value = "/rest/admin/case-instances/{caseInstanceId}/migrate")
    @ResponseStatus(value = HttpStatus.OK)
    public void migrateProcessInstance(@PathVariable String caseInstanceId, @RequestBody String migrationDocument) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            clientService.migrateCaseInstance(serverConfig, caseInstanceId, migrationDocument);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error migrating case instance {}", caseInstanceId, e);
            throw new BadRequestException(e.getMessage());
        }
    }
}
