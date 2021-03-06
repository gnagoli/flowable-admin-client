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
package org.gnagoli.flowable.admin.client.task.rest.runtime;

import org.gnagoli.flowable.admin.client.common.model.ResultListDataRepresentation;
import org.gnagoli.flowable.admin.client.task.logic.service.runtime.FlowableCaseDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing the Engine case definitions.
 */
@RestController
@RequestMapping("/app")
public class CaseDefinitionsResource {

    @Autowired
    protected FlowableCaseDefinitionService caseDefinitionService;

    @GetMapping(value = "/rest/case-definitions")
    public ResultListDataRepresentation getCaseDefinitions(@RequestParam(value = "latest", required = false) Boolean latest,
                                                           @RequestParam(value = "appDefinitionKey", required = false) String appDefinitionKey) {

        return caseDefinitionService.getCaseDefinitions(latest, appDefinitionKey);
    }

}
