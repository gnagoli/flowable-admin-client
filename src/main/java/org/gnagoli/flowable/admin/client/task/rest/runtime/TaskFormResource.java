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

import org.flowable.form.api.FormInfo;
import org.flowable.form.model.SimpleFormModel;
import org.gnagoli.flowable.admin.client.task.logic.model.runtime.CompleteFormRepresentation;
import org.gnagoli.flowable.admin.client.task.logic.model.runtime.FormModelRepresentation;
import org.gnagoli.flowable.admin.client.task.logic.model.runtime.SaveFormRepresentation;
import org.gnagoli.flowable.admin.client.task.logic.service.runtime.FlowableTaskFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Joram Barrez
 */
@RestController
@RequestMapping("/app/rest/task-forms")
public class TaskFormResource {

    @Autowired
    protected FlowableTaskFormService taskFormService;

    @GetMapping(value = "/{taskId}", produces = "application/json")
    public FormModelRepresentation getTaskForm(@PathVariable String taskId) {
        FormInfo formInfo = taskFormService.getTaskForm(taskId);
        SimpleFormModel formModel = (SimpleFormModel) formInfo.getFormModel();
        return new FormModelRepresentation(formInfo, formModel);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/{taskId}", produces = "application/json")
    public void completeTaskForm(@PathVariable String taskId, @RequestBody CompleteFormRepresentation completeTaskFormRepresentation) {
        taskFormService.completeTaskForm(taskId, completeTaskFormRepresentation);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/{taskId}/save-form", produces = "application/json")
    public void saveTaskForm(@PathVariable String taskId, @RequestBody SaveFormRepresentation saveFormRepresentation) {
        taskFormService.saveTaskForm(taskId, saveFormRepresentation);
    }
}
