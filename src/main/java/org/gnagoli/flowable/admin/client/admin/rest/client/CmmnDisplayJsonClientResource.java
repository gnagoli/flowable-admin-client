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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.cmmn.model.*;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.gnagoli.flowable.admin.client.admin.logic.domain.EndpointType;
import org.gnagoli.flowable.admin.client.admin.logic.domain.ServerConfig;
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.CaseDefinitionService;
import org.gnagoli.flowable.admin.client.admin.logic.service.engine.CaseInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class CmmnDisplayJsonClientResource extends AbstractClientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmmnDisplayJsonClientResource.class);

    @Autowired
    protected CaseDefinitionService clientService;
    
    @Autowired
    protected CaseInstanceService caseInstanceService;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(value = "/rest/admin/case-definitions/{caseDefinitionId}/model-json", produces = "application/json")
    public JsonNode getCaseDefinitionModelJSON(@PathVariable String caseDefinitionId) {

        ServerConfig config = retrieveServerConfig(EndpointType.CMMN);
        ObjectNode displayNode = objectMapper.createObjectNode();

        CmmnModel pojoModel = clientService.getCaseDefinitionModel(config, caseDefinitionId);

        if (!pojoModel.getLocationMap().isEmpty()) {
            try {
                GraphicInfo diagramInfo = new GraphicInfo();
                processCaseElements(pojoModel, displayNode, diagramInfo, null, null, null);

                displayNode.put("diagramBeginX", diagramInfo.getX());
                displayNode.put("diagramBeginY", diagramInfo.getY());
                displayNode.put("diagramWidth", diagramInfo.getWidth());
                displayNode.put("diagramHeight", diagramInfo.getHeight());

            } catch (Exception e) {
                LOGGER.error("Error creating model JSON", e);
            }
        }

        return displayNode;
    }
    
    @GetMapping(value = "/rest/admin/case-instances/{caseInstanceId}/model-json", produces = "application/json")
    public JsonNode getCaseInstanceModelJSON(@PathVariable String caseInstanceId) {
        ObjectNode displayNode = objectMapper.createObjectNode();

        ServerConfig config = retrieveServerConfig(EndpointType.CMMN);
        JsonNode caseInstanceNode = caseInstanceService.getCaseInstance(config, caseInstanceId);

        if (caseInstanceNode == null) {
            return displayNode;
        }

        String caseDefinitionId = caseInstanceNode.get("caseDefinitionId").asText();
        CmmnModel pojoModel = clientService.getCaseDefinitionModel(config, caseDefinitionId);

        if (!pojoModel.getLocationMap().isEmpty()) {
            
            JsonNode planitemsInstanceNodes = caseInstanceService.getPlanItemInstancesForCaseInstance(config, caseInstanceId);
            
            Set<String> completedPlanItemInstances = new HashSet<>();
            Set<String> activePlanItemInstances = new HashSet<>();
            Set<String> availablePlanItemInstances = new HashSet<>();
            if (planitemsInstanceNodes != null && planitemsInstanceNodes.has("data") && planitemsInstanceNodes.get("data").isArray()) {
                for (JsonNode planItemInstance : planitemsInstanceNodes.get("data")) {
                    if ((planItemInstance.has("completedTime") && !planItemInstance.get("completedTime").isNull())
                            || (planItemInstance.has("terminatedTime") && !planItemInstance.get("terminatedTime").isNull())
                            || (planItemInstance.has("occurredTime") && !planItemInstance.get("occurredTime").isNull())) {
                        
                        completedPlanItemInstances.add(planItemInstance.get("planItemDefinitionId").asText());

                    } else if ("active".equals(planItemInstance.get("state").asText())) {
                        activePlanItemInstances.add(planItemInstance.get("planItemDefinitionId").asText());

                    } else if ("available".equals(planItemInstance.get("state").asText())) {
                        availablePlanItemInstances.add(planItemInstance.get("planItemDefinitionId").asText());
                    }
                }

            }

            GraphicInfo diagramInfo = new GraphicInfo();
            try {
                processCaseElements(pojoModel, displayNode, diagramInfo, completedPlanItemInstances, activePlanItemInstances, availablePlanItemInstances);
                displayNode.put("diagramBeginX", diagramInfo.getX());
                displayNode.put("diagramBeginY", diagramInfo.getY());
                displayNode.put("diagramWidth", diagramInfo.getWidth());
                displayNode.put("diagramHeight", diagramInfo.getHeight());
                
                ArrayNode completedActivities = displayNode.putArray("completedActivities");
                for (String completed : completedPlanItemInstances) {
                    completedActivities.add(completed);
                }

                ArrayNode currentActivities = displayNode.putArray("currentActivities");
                for (String current : activePlanItemInstances) {
                    currentActivities.add(current);
                }

                ArrayNode availableActivities = displayNode.putArray("availableActivities");
                for (String available : availablePlanItemInstances) {
                    availableActivities.add(available);
                }
                
            } catch (Exception e) {
                LOGGER.error("Error creating model JSON", e);
            }
        }

        return displayNode;
    }

    protected void processCaseElements(CmmnModel pojoModel, ObjectNode displayNode, GraphicInfo diagramInfo,
            Set<String> completedElements, Set<String> activeElements, Set<String> availableElements) throws Exception {

        if (pojoModel.getLocationMap().isEmpty()) {
            return;
        }

        ArrayNode elementArray = objectMapper.createArrayNode();
        ArrayNode flowArray = objectMapper.createArrayNode();

        // in initialize with fake x and y to make sure the minimal values are set
        diagramInfo.setX(9999);
        diagramInfo.setY(1000);

        for (Case caseObject : pojoModel.getCases()) {
            ObjectNode elementNode = objectMapper.createObjectNode();
            elementNode.put("id", caseObject.getPlanModel().getId());
            elementNode.put("name", caseObject.getPlanModel().getName());

            GraphicInfo graphicInfo = pojoModel.getGraphicInfo(caseObject.getPlanModel().getId());
            if (graphicInfo != null) {
                fillGraphicInfo(elementNode, graphicInfo, true);
                fillDiagramInfo(graphicInfo, diagramInfo);
            }

            elementNode.put("type", "PlanModel");
            elementArray.add(elementNode);

            processCriteria(caseObject.getPlanModel().getExitCriteria(), "ExitCriterion", pojoModel, elementArray);

            processElements(caseObject.getPlanModel().getPlanItems(), pojoModel, elementArray, flowArray, 
                    completedElements, activeElements, availableElements, diagramInfo);
        }

        for (Association association : pojoModel.getAssociations()) {
            ObjectNode elementNode = objectMapper.createObjectNode();
            elementNode.put("id", association.getId());
            elementNode.put("type", "Association");
            elementNode.put("sourceRef", association.getSourceRef());
            elementNode.put("targetRef", association.getTargetRef());
            List<GraphicInfo> flowInfo = pojoModel.getFlowLocationGraphicInfo(association.getId());
            if (CollectionUtils.isNotEmpty(flowInfo)) {
                ArrayNode waypointArray = objectMapper.createArrayNode();
                for (GraphicInfo graphicInfo : flowInfo) {
                    ObjectNode pointNode = objectMapper.createObjectNode();
                    fillGraphicInfo(pointNode, graphicInfo, false);
                    waypointArray.add(pointNode);
                    fillDiagramInfo(graphicInfo, diagramInfo);
                }
                elementNode.set("waypoints", waypointArray);

                flowArray.add(elementNode);
            }
        }

        displayNode.set("elements", elementArray);
        displayNode.set("flows", flowArray);

        displayNode.put("diagramBeginX", diagramInfo.getX());
        displayNode.put("diagramBeginY", diagramInfo.getY());
        displayNode.put("diagramWidth", diagramInfo.getWidth());
        displayNode.put("diagramHeight", diagramInfo.getHeight());
    }

    protected void processElements(List<PlanItem> planItemList, CmmnModel model, ArrayNode elementArray, ArrayNode flowArray, 
            Set<String> completedElements, Set<String> activeElements, Set<String> availableElements, GraphicInfo diagramInfo) {

        for (PlanItem planItem : planItemList) {
            ObjectNode elementNode = objectMapper.createObjectNode();
            elementNode.put("id", planItem.getId());
            elementNode.put("name", planItem.getName());

            GraphicInfo graphicInfo = model.getGraphicInfo(planItem.getId());
            if (graphicInfo != null) {
                fillGraphicInfo(elementNode, graphicInfo, true);
                fillDiagramInfo(graphicInfo, diagramInfo);
            }

            PlanItemDefinition planItemDefinition = planItem.getPlanItemDefinition();
            String className = planItemDefinition.getClass().getSimpleName();
            elementNode.put("type", className);
            elementNode.put("planItemDefinitionId", planItemDefinition.getId());
            
            if (completedElements != null) {
                elementNode.put("completed", completedElements.contains(planItemDefinition.getId()));
            }

            if (activeElements != null) {
                elementNode.put("current", activeElements.contains(planItemDefinition.getId()));
            }

            if (availableElements != null) {
                elementNode.put("available", availableElements.contains(planItemDefinition.getId()));
            }

            if (planItemDefinition instanceof ServiceTask) {
                ServiceTask serviceTask = (ServiceTask) planItemDefinition;
                if (HttpServiceTask.HTTP_TASK.equals(serviceTask.getType())) {
                    elementNode.put("taskType", "http");
                }
            }

            elementArray.add(elementNode);

            processCriteria(planItem.getEntryCriteria(), "EntryCriterion", model, elementArray);
            processCriteria(planItem.getExitCriteria(), "ExitCriterion", model, elementArray);

            if (planItemDefinition instanceof Stage) {
                Stage stage = (Stage) planItemDefinition;

                processElements(stage.getPlanItems(), model, elementArray, flowArray, completedElements,
                        activeElements, availableElements, diagramInfo);
            }
        }
    }

    protected void processCriteria(List<Criterion> criteria, String type, CmmnModel model, ArrayNode elementArray) {
        for (Criterion criterion : criteria) {
            ObjectNode criterionNode = objectMapper.createObjectNode();
            criterionNode.put("id", criterion.getId());
            criterionNode.put("name", criterion.getName());
            criterionNode.put("type", type);

            GraphicInfo criterionGraphicInfo = model.getGraphicInfo(criterion.getId());
            if (criterionGraphicInfo != null) {
                fillGraphicInfo(criterionNode, criterionGraphicInfo, true);
            }

            elementArray.add(criterionNode);
        }
    }

    protected void fillWaypoints(String id, CmmnModel model, ObjectNode elementNode, GraphicInfo diagramInfo) {
        List<GraphicInfo> flowInfo = model.getFlowLocationGraphicInfo(id);
        ArrayNode waypointArray = objectMapper.createArrayNode();
        for (GraphicInfo graphicInfo : flowInfo) {
            ObjectNode pointNode = objectMapper.createObjectNode();
            fillGraphicInfo(pointNode, graphicInfo, false);
            waypointArray.add(pointNode);
            fillDiagramInfo(graphicInfo, diagramInfo);
        }
        elementNode.set("waypoints", waypointArray);
    }

    protected void fillGraphicInfo(ObjectNode elementNode, GraphicInfo graphicInfo, boolean includeWidthAndHeight) {
        commonFillGraphicInfo(elementNode, graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight(), includeWidthAndHeight);
    }

    protected void commonFillGraphicInfo(ObjectNode elementNode, double x, double y, double width, double height, boolean includeWidthAndHeight) {

        elementNode.put("x", x);
        elementNode.put("y", y);
        if (includeWidthAndHeight) {
            elementNode.put("width", width);
            elementNode.put("height", height);
        }
    }

    protected void fillDiagramInfo(GraphicInfo graphicInfo, GraphicInfo diagramInfo) {
        double rightX = graphicInfo.getX() + graphicInfo.getWidth();
        double bottomY = graphicInfo.getY() + graphicInfo.getHeight();
        double middleX = graphicInfo.getX() + (graphicInfo.getWidth() / 2);
        if (middleX < diagramInfo.getX()) {
            diagramInfo.setX(middleX);
        }
        if (graphicInfo.getY() < diagramInfo.getY()) {
            diagramInfo.setY(graphicInfo.getY());
        }
        if (rightX > diagramInfo.getWidth()) {
            diagramInfo.setWidth(rightX);
        }
        if (bottomY > diagramInfo.getHeight()) {
            diagramInfo.setHeight(bottomY);
        }
    }
}
