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
package org.gnagoli.flowable.admin.client.admin.logic.repository;

import org.gnagoli.flowable.admin.client.admin.logic.domain.EndpointType;
import org.gnagoli.flowable.admin.client.admin.logic.domain.ServerConfig;
import org.gnagoli.flowable.admin.client.common.repository.UuidIdGenerator;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServerConfigRepositoryImpl implements ServerConfigRepository {

    private static final String NAMESPACE = "org.gnagoli.flowable.admin.client.admin.logic.domain.ServerConfig.";

    @Autowired
    @Qualifier("flowableAdmin")
    protected SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    protected UuidIdGenerator idGenerator;

    @Override
    public ServerConfig get(String id) {
        return sqlSessionTemplate.selectOne(NAMESPACE + "selectServerConfig", id);
    }

    @Override
    public List<ServerConfig> getAll() {
        return sqlSessionTemplate.selectList(NAMESPACE + "selectAllServerConfigs");
    }

    @Override
    public List<ServerConfig> getByEndpointType(EndpointType endpointType) {
        return sqlSessionTemplate.selectList(NAMESPACE + "selectAllServerConfigsByEndpointType", endpointType.getEndpointCode());
    }

    @Override
    public void save(ServerConfig serverConfig) {
        if (serverConfig.getId() == null) {
            serverConfig.setId(idGenerator.generateId());
            sqlSessionTemplate.insert(NAMESPACE + "insertServerConfig", serverConfig);
        } else {
            sqlSessionTemplate.update(NAMESPACE + "updateServerConfig", serverConfig);
        }
    }

}
