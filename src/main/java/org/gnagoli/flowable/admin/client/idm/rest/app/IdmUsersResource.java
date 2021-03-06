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

import org.flowable.idm.api.User;
import org.gnagoli.flowable.admin.client.common.model.ResultListDataRepresentation;
import org.gnagoli.flowable.admin.client.common.model.UserRepresentation;
import org.gnagoli.flowable.admin.client.idm.logic.model.CreateUserRepresentation;
import org.gnagoli.flowable.admin.client.idm.logic.model.UpdateUsersRepresentation;
import org.gnagoli.flowable.admin.client.idm.logic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Frederik Heremans
 * @author Joram Barrez
 */
@RestController
public class IdmUsersResource {

    @Autowired
    protected UserService userService;

    @GetMapping(value = "/rest/admin/users")
    public ResultListDataRepresentation getUsers(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer start,
            @RequestParam(required = false) String groupId) {

        int startValue = start != null ? start.intValue() : 0;

        List<User> users = userService.getUsers(filter, sort, start);
        ResultListDataRepresentation result = new ResultListDataRepresentation();
        result.setTotal(userService.getUserCount(filter, sort, startValue, groupId));
        result.setStart(startValue);
        result.setSize(users.size());
        result.setData(convertToUserRepresentations(users));
        return result;
    }

    protected List<UserRepresentation> convertToUserRepresentations(List<User> users) {
        List<UserRepresentation> result = new ArrayList<>(users.size());
        for (User user : users) {
            result.add(new UserRepresentation(user));
        }
        return result;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(value = "/rest/admin/users/{userId}")
    public void updateUserDetails(@PathVariable String userId, @RequestBody UpdateUsersRepresentation updateUsersRepresentation) {
        userService.updateUserDetails(userId, updateUsersRepresentation.getFirstName(),
                updateUsersRepresentation.getLastName(),
                updateUsersRepresentation.getEmail(),
                updateUsersRepresentation.getTenantId());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @PutMapping(value = "/rest/admin/users")
    public void bulkUpdateUserDetails(@RequestBody UpdateUsersRepresentation updateUsersRepresentation) {
        userService.bulkUpdatePassword(updateUsersRepresentation.getUsers(), updateUsersRepresentation.getPassword());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @DeleteMapping(value = "/rest/admin/users/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }

    @PostMapping(value = "/rest/admin/users")
    public UserRepresentation createNewUser(@RequestBody CreateUserRepresentation userRepresentation) {
        return new UserRepresentation(userService.createNewUser(
                userRepresentation.getId(),
                userRepresentation.getFirstName(),
                userRepresentation.getLastName(),
                userRepresentation.getEmail(),
                userRepresentation.getPassword(),
                userRepresentation.getTenantId()));
    }

}
