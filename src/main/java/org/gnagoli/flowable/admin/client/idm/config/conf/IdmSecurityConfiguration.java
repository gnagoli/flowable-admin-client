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
package org.gnagoli.flowable.admin.client.idm.config.conf;

import org.gnagoli.flowable.admin.client.common.properties.FlowableRestAppProperties;
import org.gnagoli.flowable.admin.client.common.security.ApiHttpSecurityCustomizer;
import org.gnagoli.flowable.admin.client.common.security.DefaultPrivileges;
import org.gnagoli.flowable.admin.client.common.security.SecurityConstants;
import org.gnagoli.flowable.admin.client.idm.config.security.UserDetailsService;
import org.gnagoli.flowable.admin.client.idm.logic.properties.FlowableIdmAppProperties;
import org.flowable.idm.api.IdmIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Based on http://docs.spring.io/spring-security/site/docs/3.2.x/reference/htmlsingle/#multiple-httpsecurity
 *
 * @author Joram Barrez
 * @author Tijs Rademakers
 * @author Filip Hrisafov
 */
@Configuration(proxyBeanMethods = false)
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class IdmSecurityConfiguration {

    //
    // GLOBAL CONFIG
    //

    @Autowired
    protected IdmIdentityService identityService;
    
    @Autowired
    protected FlowableIdmAppProperties idmAppProperties;
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetailsService userDetailsService = new UserDetailsService();
        userDetailsService.setUserValidityPeriod(idmAppProperties.getSecurity().getUserValidityPeriod());
        return userDetailsService;
    }

    //
    // BASIC AUTH
    //

    @Configuration
    @Order(SecurityConstants.IDM_API_SECURITY_ORDER)
    public static class IdmApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        protected final FlowableRestAppProperties restAppProperties;
        protected final FlowableIdmAppProperties idmAppProperties;
        protected final ApiHttpSecurityCustomizer apiHttpSecurityCustomizer;

        public IdmApiWebSecurityConfigurationAdapter(FlowableRestAppProperties restAppProperties,
                FlowableIdmAppProperties idmAppProperties, ApiHttpSecurityCustomizer apiHttpSecurityCustomizer) {
            this.restAppProperties = restAppProperties;
            this.idmAppProperties = idmAppProperties;
            this.apiHttpSecurityCustomizer = apiHttpSecurityCustomizer;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf()
                    .disable();

            if (idmAppProperties.isRestEnabled()) {

                if (restAppProperties.isVerifyRestApiPrivilege()) {
                    http.antMatcher("/api/idm/**").authorizeRequests().antMatchers("/api/idm/**").hasAuthority(DefaultPrivileges.ACCESS_REST_API);
                } else {
                    http.antMatcher("/api/idm/**").authorizeRequests().antMatchers("/api/idm/**").authenticated();
                    
                }

                apiHttpSecurityCustomizer.customize(http);
                
            } else {
                http.antMatcher("/api/idm/**").authorizeRequests().antMatchers("/api/idm/**").denyAll();
                
            }
            
        }
    }
}
