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
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.CustomCmmnTaskService;
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.exception.FlowableServiceException;
import org.gnagoli.flowable.admin.client.common.service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class CmmnTaskClientResource extends AbstractClientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmmnTaskClientResource.class);

    @Autowired
    protected CustomCmmnTaskService clientService;

    @GetMapping(value = "/rest/admin/cmmn-tasks/{taskId}", produces = "application/json")
    public JsonNode getTask(@PathVariable String taskId, @RequestParam(required = false, defaultValue = "false") boolean runtime) {

        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            return clientService.getTask(serverConfig, taskId, runtime);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting task {}", taskId);
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping(value = "/rest/admin/cmmn-tasks/{taskId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            clientService.deleteTask(serverConfig, taskId);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error deleting task {}", taskId);
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping(value = "/rest/admin/cmmn-tasks/{taskId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void executeTaskAction(@PathVariable String taskId, @RequestBody ObjectNode actionBody) {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            clientService.executeTaskAction(serverConfig, taskId, actionBody);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error executing action on task {}", taskId);
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping(value = "/rest/admin/cmmn-tasks/{taskId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateTask(@PathVariable String taskId, @RequestBody ObjectNode actionBody) {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            clientService.updateTask(serverConfig, taskId, actionBody);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error updating task {}", taskId);
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(value = "/rest/admin/cmmn-tasks/{taskId}/subtasks")
    public JsonNode getSubtasks(@PathVariable String taskId) {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            return clientService.getSubTasks(serverConfig, taskId);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting sub tasks {}", taskId);
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(value = "/rest/admin/cmmn-tasks/{taskId}/variables")
    public JsonNode getVariables(@PathVariable String taskId) {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            return clientService.getVariables(serverConfig, taskId);
        } catch (FlowableServiceException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping(value = "/rest/admin/cmmn-tasks/{taskId}/identitylinks")
    public JsonNode getIdentityLinks(@PathVariable String taskId) {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.CMMN);
        try {
            return clientService.getIdentityLinks(serverConfig, taskId);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting identity links for task {}", taskId);
            throw new BadRequestException(e.getMessage());
        }
    }

}
