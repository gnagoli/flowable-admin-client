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
package org.gnagoli.flowable.admin.client.idm.rest.api;

import org.flowable.idm.api.Token;
import org.gnagoli.flowable.admin.client.common.service.exception.NotFoundException;
import org.gnagoli.flowable.admin.client.idm.logic.model.TokenRepresentation;
import org.gnagoli.flowable.admin.client.idm.logic.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTokensResource {

    @Autowired
    protected TokenService tokenService;

    @GetMapping(value = "/tokens/{tokenId}", produces = { "application/json" })
    public TokenRepresentation getToken(@PathVariable String tokenId) {
        Token token = tokenService.findTokenById(tokenId);
        if (token == null) {
            throw new NotFoundException();
        } else {
            return new TokenRepresentation(token);
        }
    }

}
