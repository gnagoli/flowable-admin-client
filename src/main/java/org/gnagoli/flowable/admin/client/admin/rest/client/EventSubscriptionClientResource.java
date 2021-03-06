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
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.EventSubscriptionService;
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.exception.FlowableServiceException;
import org.gnagoli.flowable.admin.client.common.service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing an event subscription.
 */
@RestController
public class EventSubscriptionClientResource extends AbstractClientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSubscriptionClientResource.class);

    @Autowired
    protected EventSubscriptionService eventSubscriptionService;

    /**
     * GET /rest/admin/event-subscriptions/{eventSubscriptionId} -> return event subscription data
     */
    @GetMapping(value = "/rest/admin/event-subscriptions/{eventSubscriptionId}", produces = "application/json")
    public JsonNode getEventSubscription(@PathVariable String eventSubscriptionId) throws BadRequestException {

        ServerConfig serverConfig = retrieveServerConfig(EndpointType.PROCESS);
        try {
            return eventSubscriptionService.getEventSubscription(serverConfig, eventSubscriptionId);

        } catch (FlowableServiceException e) {
            LOGGER.error("Error getting event subscription {}", eventSubscriptionId, e);
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * POST /rest/admin/event-subscriptions/{eventSubscriptionId} -> trigger event
     */
    @PostMapping(value = "/rest/admin/event-subscriptions/{eventSubscriptionId}", produces = "application/json")
    @ResponseStatus(value = HttpStatus.OK)
    public void triggerEvent(@PathVariable String eventSubscriptionId, @RequestBody ObjectNode eventBody) throws BadRequestException {

        ServerConfig serverConfig = retrieveServerConfig(EndpointType.PROCESS);
        String eventType = eventBody.get("eventType").asText();
        String eventName = eventBody.get("eventName").asText();

        if (eventBody.has("executionId") && !eventBody.get("executionId").isNull()) {
            try {
                eventSubscriptionService.triggerExecutionEvent(serverConfig, eventType, eventName,
                        eventBody.get("executionId").asText());

            } catch (FlowableServiceException e) {
                LOGGER.error("Error triggering execution event for event subscription {}", eventSubscriptionId, e);
                throw new BadRequestException(e.getMessage());
            }

        } else if ("message".equals(eventType)) {
            try {
                String tenantId = null;
                if (eventBody.has("tenantId") && !eventBody.get("tenantId").isNull()) {
                    tenantId = eventBody.get("tenantId").asText();
                }
                eventSubscriptionService.triggerMessageEvent(serverConfig, eventName, tenantId);

            } catch (FlowableServiceException e) {
                LOGGER.error("Error triggering message event for event subscription {}", eventSubscriptionId, e);
                throw new BadRequestException(e.getMessage());
            }

        } else if ("signal".equals(eventType)) {
            try {
                eventSubscriptionService.triggerSignalEvent(serverConfig, eventName);

            } catch (FlowableServiceException e) {
                LOGGER.error("Error triggering signal event for event subscription {}", eventSubscriptionId, e);
                throw new BadRequestException(e.getMessage());
            }

        } else {
            throw new FlowableServiceException("Unsupported event type " + eventType);
        }
    }
}
