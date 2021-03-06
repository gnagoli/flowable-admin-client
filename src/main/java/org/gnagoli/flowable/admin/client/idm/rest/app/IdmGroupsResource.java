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
package org.gnagoli.flowable.admin.client.idm.rest.app;

import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.gnagoli.flowable.admin.client.common.model.GroupRepresentation;
import org.gnagoli.flowable.admin.client.common.model.ResultListDataRepresentation;
import org.gnagoli.flowable.admin.client.common.model.UserRepresentation;
import org.gnagoli.flowable.admin.client.idm.logic.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joram Barrez
 */
@RestController
@RequestMapping(value = "/rest/admin/groups")
public class IdmGroupsResource {

    @Autowired
    private GroupService groupService;

    @GetMapping()
    public List<GroupRepresentation> getGroups(@RequestParam(required = false) String filter) {
        List<GroupRepresentation> result = new ArrayList<>();
        for (Group group : groupService.getGroups(filter)) {
            result.add(new GroupRepresentation(group));
        }
        return result;
    }

    @GetMapping(value = "/{groupId}")
    public GroupRepresentation getGroup(@PathVariable String groupId) {
        return new GroupRepresentation(groupService.getGroup(groupId));
    }

    @GetMapping(value = "/{groupId}/users")
    public ResultListDataRepresentation getGroupUsers(@PathVariable String groupId,
                                                      @RequestParam(required = false) String filter,
                                                      @RequestParam(required = false) Integer page,
                                                      @RequestParam(required = false) Integer pageSize) {

        List<User> users = groupService.getGroupUsers(groupId, filter, page, pageSize);
        List<UserRepresentation> userRepresentations = new ArrayList<>(users.size());
        for (User user : users) {
            userRepresentations.add(new UserRepresentation(user));
        }

        ResultListDataRepresentation resultListDataRepresentation = new ResultListDataRepresentation(userRepresentations);
        resultListDataRepresentation.setStart(page * pageSize);
        resultListDataRepresentation.setSize(userRepresentations.size());
        resultListDataRepresentation.setTotal(groupService.countTotalGroupUsers(groupId, filter, page, pageSize));
        return resultListDataRepresentation;
    }

    @PostMapping()
    public GroupRepresentation createNewGroup(@RequestBody GroupRepresentation groupRepresentation) {
        return new GroupRepresentation(groupService.createNewGroup(groupRepresentation.getId(), groupRepresentation.getName(), groupRepresentation.getType()));
    }

    @PutMapping(value = "/{groupId}")
    public GroupRepresentation updateGroup(@PathVariable String groupId, @RequestBody GroupRepresentation groupRepresentation) {
        return new GroupRepresentation(groupService.updateGroupName(groupId, groupRepresentation.getName()));
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "/{groupId}")
    public void deleteGroup(@PathVariable String groupId) {
        groupService.deleteGroup(groupId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping(value = "/{groupId}/members/{userId}")
    public void addGroupMember(@PathVariable String groupId, @PathVariable String userId) {
        groupService.addGroupMember(groupId, userId);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "/{groupId}/members/{userId}")
    public void deleteGroupMember(@PathVariable String groupId, @PathVariable String userId) {
        groupService.deleteGroupMember(groupId, userId);
    }

}
