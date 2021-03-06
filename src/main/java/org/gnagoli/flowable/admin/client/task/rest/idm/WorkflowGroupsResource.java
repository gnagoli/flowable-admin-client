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
import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.api.IdmIdentityService;
import org.gnagoli.flowable.admin.client.common.model.GroupRepresentation;
import org.gnagoli.flowable.admin.client.common.model.ResultListDataRepresentation;
import org.gnagoli.flowable.admin.client.common.service.idm.RemoteIdmService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/app")
public class WorkflowGroupsResource implements InitializingBean {

    @Autowired(required = false)
    private RemoteIdmService remoteIdmService;

    @Autowired(required = false)
    private IdmIdentityService identityService;

    @Override
    public void afterPropertiesSet() {
        if (remoteIdmService == null && identityService == null) {
            throw new FlowableIllegalStateException("No remoteIdmService or identityService have been provided");
        }
    }

    @GetMapping(value = "/rest/workflow-groups")
    public ResultListDataRepresentation getGroups(@RequestParam(value = "filter", required = false) String filter) {
        List<? extends Group> matchingGroups;
        if (remoteIdmService != null) {
            matchingGroups = remoteIdmService.findGroupsByNameFilter(filter);
        } else {
            GroupQuery groupQuery = identityService.createGroupQuery();
            if (StringUtils.isNotEmpty(filter)) {
                groupQuery.groupNameLikeIgnoreCase("%" + filter + "%");
            }
            matchingGroups = groupQuery.orderByGroupName().asc().list();
        }
        List<GroupRepresentation> groupRepresentations = new ArrayList<>();
        for (Group group : matchingGroups) {
            groupRepresentations.add(new GroupRepresentation(group));
        }
        return new ResultListDataRepresentation(groupRepresentations);
    }

}
