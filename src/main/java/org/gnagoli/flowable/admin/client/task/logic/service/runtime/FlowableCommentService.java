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
package org.gnagoli.flowable.admin.client.task.logic.service.runtime;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.runtime.Clock;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.gnagoli.flowable.admin.client.common.model.ResultListDataRepresentation;
import org.gnagoli.flowable.admin.client.common.security.SecurityScope;
import org.gnagoli.flowable.admin.client.common.security.SecurityUtils;
import org.gnagoli.flowable.admin.client.common.service.exception.BadRequestException;
import org.gnagoli.flowable.admin.client.common.service.exception.NotFoundException;
import org.gnagoli.flowable.admin.client.common.service.exception.NotPermittedException;
import org.gnagoli.flowable.admin.client.task.logic.model.runtime.CommentRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tijs Rademakers
 */
@Service
@Transactional
public class FlowableCommentService {

    @Autowired
    protected PermissionService permissionService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected Clock clock;

    public ResultListDataRepresentation getTaskComments(String taskId) {

        SecurityScope currentUser = SecurityUtils.getAuthenticatedSecurityScope();
        checkReadPermissionOnTask(currentUser, taskId);
        List<Comment> comments = getCommentsForTask(taskId);

        // Create representation for all comments
        List<CommentRepresentation> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            commentList.add(new CommentRepresentation(comment));
        }

        return new ResultListDataRepresentation(commentList);
    }

    public CommentRepresentation addTaskComment(CommentRepresentation commentRequest, String taskId) {

        if (StringUtils.isBlank(commentRequest.getMessage())) {
            throw new BadRequestException("Comment should not be empty");
        }

        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new NotFoundException("No task found with id: " + taskId);
        }

        // Check read permission and message
        SecurityScope currentUser = SecurityUtils.getAuthenticatedSecurityScope();
        checkReadPermissionOnTask(currentUser, taskId);

        // Create comment
        Comment comment = createComment(commentRequest.getMessage(), currentUser, task.getId(), task.getProcessInstanceId());
        return new CommentRepresentation(comment);
    }

    public ResultListDataRepresentation getProcessInstanceComments(String processInstanceId) {

        SecurityScope currentUser = SecurityUtils.getAuthenticatedSecurityScope();
        checkReadPermissionOnProcessInstance(currentUser, processInstanceId);
        List<Comment> comments = getCommentsForProcessInstance(processInstanceId);

        // Create representation for all comments
        List<CommentRepresentation> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            commentList.add(new CommentRepresentation(comment));
        }

        return new ResultListDataRepresentation(commentList);
    }

    public CommentRepresentation addProcessInstanceComment(CommentRepresentation commentRequest, String processInstanceId) {

        if (StringUtils.isBlank(commentRequest.getMessage())) {
            throw new BadRequestException("Comment should not be empty");
        }

        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new NotFoundException("No process instance found with id: " + processInstanceId);
        }

        // Check read permission and message
        SecurityScope currentUser = SecurityUtils.getAuthenticatedSecurityScope();
        checkReadPermissionOnProcessInstance(currentUser, processInstanceId);

        // Create comment
        Comment comment = createComment(commentRequest.getMessage(), currentUser, processInstanceId);
        return new CommentRepresentation(comment);
    }

    public Long countCommentsForTask(String taskId) {
        return (long) taskService.getTaskComments(taskId).size();
    }

    public Long countCommentsForProcessInstance(String processInstanceId) {
        return (long) taskService.getProcessInstanceComments(processInstanceId).size();
    }

    public List<Comment> getCommentsForTask(String taskId) {
        return taskService.getTaskComments(taskId);
    }

    public List<Comment> getCommentsForProcessInstance(String processInstanceId) {
        return taskService.getProcessInstanceComments(processInstanceId);
    }

    public Comment createComment(String message, SecurityScope createdBy, String processInstanceId) {
        return createComment(message, createdBy, null, processInstanceId);
    }

    public Comment createComment(String message, SecurityScope createdBy, String taskId, String processInstanceId) {
        return taskService.addComment(taskId, processInstanceId, message);
    }

    public void deleteComment(Comment comment) {
        taskService.deleteComment(comment.getId());
    }

    /**
     * Deletes all comments related to the given process instance. Includes both comments on the process instance itself and any comments on the tasks in that process.
     */
    public void deleteAllCommentsForProcessInstance(String processInstanceId) {
        taskService.deleteComments(null, processInstanceId);
    }

    protected void checkReadPermissionOnTask(SecurityScope user, String taskId) {
        if (taskId == null) {
            throw new BadRequestException("Task id is required");
        }
        permissionService.validateReadPermissionOnTask(user, taskId);
    }

    protected void checkReadPermissionOnProcessInstance(SecurityScope user, String processInstanceId) {
        if (processInstanceId == null) {
            throw new BadRequestException("Process instance id is required");
        }
        if (!permissionService.hasReadPermissionOnProcessInstance(user, processInstanceId)) {
            throw new NotPermittedException("You are not permitted to read process instance with id: " + processInstanceId);
        }
    }
}
