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
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.TaskService;
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.exception.FlowableServiceException;
import org.gnagoli.flowable.admin.client.common.service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the current user's account.
 */
@RestController
public class TasksClientResource extends AbstractClientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TasksClientResource.class);

    @Autowired
    protected TaskService clientService;

    @PostMapping(value = "/rest/admin/tasks", produces = "application/json")
    public JsonNode listTasks(@RequestBody ObjectNode requestNode) {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.PROCESS);
        JsonNode resultNode;
        try {
            resultNode = clientService.listTasks(serverConfig, requestNode);
        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting tasks", e);
            throw new BadRequestException(e.getMessage());
        }

        return resultNode;
    }
}
