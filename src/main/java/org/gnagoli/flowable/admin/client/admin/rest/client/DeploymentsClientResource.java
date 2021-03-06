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
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.DeploymentService;
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.exception.FlowableServiceException;
import org.gnagoli.flowable.admin.client.common.service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author jbarrez
 */
@RestController
@RequestMapping("/rest/admin/deployments")
public class DeploymentsClientResource extends AbstractClientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentsClientResource.class);

    @Autowired
    protected DeploymentService clientService;

    /**
     * GET /rest/admin/deployments -> get a list of deployments.
     */
    @GetMapping(produces = "application/json")
    public JsonNode listDeployments(HttpServletRequest request) {
        LOGGER.debug("REST request to get a list of deployments");

        JsonNode resultNode = null;
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.PROCESS);
        Map<String, String[]> parameterMap = getRequestParametersWithoutServerId(request);

        try {
            resultNode = clientService.listDeployments(serverConfig, parameterMap);

        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting deployments", e);
            throw new BadRequestException(e.getMessage());
        }

        return resultNode;
    }

    /**
     * POST /rest/admin/deployments: upload a deployment
     */
    @PostMapping(produces = "application/json")
    public JsonNode handleFileUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                ServerConfig serverConfig = retrieveServerConfig(EndpointType.PROCESS);
                String fileName = file.getOriginalFilename();
                if (fileName != null && (fileName.endsWith(".bpmn") || fileName.endsWith(".bpmn20.xml") || fileName.endsWith(".zip") || fileName.endsWith(".bar"))) {
                    return clientService.uploadDeployment(serverConfig, fileName, file.getInputStream());

                } else {
                    LOGGER.error("Invalid filename {}", fileName);
                    throw new BadRequestException("Invalid file name");
                }

            } catch (Exception e) {
                LOGGER.error("Error deploying file", e);
                throw new BadRequestException("Could not deploy file: " + e.getMessage());
            }

        } else {
            LOGGER.error("No file found in POST request");
            throw new BadRequestException("No file found in POST body");
        }
    }

}
