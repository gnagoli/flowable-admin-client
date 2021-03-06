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
package org.gnagoli.flowable.admin.client.task.config.conf;

import org.gnagoli.flowable.admin.client.task.logic.properties.FlowableTaskAppProperties;
import org.gnagoli.flowable.admin.client.task.logic.service.runtime.FlowableAppDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Responsible for executing all action required after booting up the Spring container.
 * 
 * @author Joram Barrez
 * @author Tijs Rademakers
 */
@Component
public class TaskBootstrapper implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskBootstrapper.class);

    @Autowired
    private FlowableAppDefinitionService appDefinitionService;

    @Autowired
    private FlowableTaskAppProperties taskAppProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) { // Using Spring MVC, there are multiple child contexts. We only care about the root

            if (taskAppProperties == null || taskAppProperties.isAppMigrationEnabled()) {
                String appMigrationResult = appDefinitionService.migrateAppDefinitions();
                LOGGER.info(appMigrationResult);
            } 
        }
    }
}
