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
package org.gnagoli.flowable.admin.client.task.rest.idm;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableIllegalStateException;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.gnagoli.flowable.admin.client.common.model.ResultListDataRepresentation;
import org.gnagoli.flowable.admin.client.common.model.UserRepresentation;
import org.gnagoli.flowable.admin.client.common.service.idm.RemoteIdmService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Rest resource for managing users, specifically related to tasks and processes.
 */
@RestController
@RequestMapping("/app")
public class WorkflowUsersResource implements InitializingBean {

    private static final int MAX_USER_SIZE = 100;

    @Autowired(required = false)
    private RemoteIdmService remoteIdmService;

    @Autowired(required = false)
    private IdmIdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Override
    public void afterPropertiesSet() {
        if (remoteIdmService == null && identityService == null) {
            throw new FlowableIllegalStateException("No remoteIdmService or identityService have been provided");
        }
    }

    @GetMapping(value = "/rest/workflow-users")
    public ResultListDataRepresentation getUsers(@RequestParam(value = "filter", required = false) String filter,
                                                 @RequestParam(value = "excludeTaskId", required = false) String excludeTaskId,
                                                 @RequestParam(value = "excludeProcessId", required = false) String excludeProcessId) {

        List<? extends User> matchingUsers;
        if (remoteIdmService != null) {
            matchingUsers = remoteIdmService.findUsersByNameFilter(filter);
        } else {
            UserQuery userQuery = identityService.createUserQuery();
            if (StringUtils.isNotEmpty(filter)) {
                userQuery.userFullNameLikeIgnoreCase("%" + filter + "%");
            }

            matchingUsers = userQuery.listPage(0, MAX_USER_SIZE);
        }

        // Filter out users already part of the task/process of which the ID has been passed
        if (excludeTaskId != null) {
            filterUsersInvolvedInTask(excludeTaskId, matchingUsers);
        } else if (excludeProcessId != null) {
            filterUsersInvolvedInProcess(excludeProcessId, matchingUsers);
        }

        List<UserRepresentation> userRepresentations = new ArrayList<>(matchingUsers.size());
        for (User user : matchingUsers) {
            userRepresentations.add(new UserRepresentation(user));
        }

        return new ResultListDataRepresentation(userRepresentations);

    }

    protected void filterUsersInvolvedInProcess(String excludeProcessId, List<? extends User> matchingUsers) {
        Set<String> involvedUsers = getInvolvedUsersAsSet(
                runtimeService.getIdentityLinksForProcessInstance(excludeProcessId));
        removeinvolvedUsers(matchingUsers, involvedUsers);
    }

    protected void filterUsersInvolvedInTask(String excludeTaskId, List<? extends User> matchingUsers) {
        Set<String> involvedUsers = getInvolvedUsersAsSet(taskService.getIdentityLinksForTask(excludeTaskId));
        removeinvolvedUsers(matchingUsers, involvedUsers);
    }

    protected Set<String> getInvolvedUsersAsSet(List<IdentityLink> involvedPeople) {
        Set<String> involved = null;
        if (involvedPeople.size() > 0) {
            involved = new HashSet<>();
            for (IdentityLink link : involvedPeople) {
                if (link.getUserId() != null) {
                    involved.add(link.getUserId());
                }
            }
        }
        return involved;
    }

    protected void removeinvolvedUsers(List<? extends User> matchingUsers, Set<String> involvedUsers) {
        if (involvedUsers != null) {
            // Using iterator to be able to remove without ConcurrentModExceptions
            Iterator<? extends User> userIt = matchingUsers.iterator();
            while (userIt.hasNext()) {
                if (involvedUsers.contains(userIt.next().getId())) {
                    userIt.remove();
                }
            }
        }
    }

}
