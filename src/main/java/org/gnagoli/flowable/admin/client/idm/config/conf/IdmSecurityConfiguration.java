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

import org.flowable.idm.api.IdmIdentityService;
import org.gnagoli.flowable.admin.client.common.properties.FlowableRestAppProperties;
import org.gnagoli.flowable.admin.client.common.security.ApiHttpSecurityCustomizer;
import org.gnagoli.flowable.admin.client.common.security.SecurityConstants;
import org.gnagoli.flowable.admin.client.idm.config.security.UserDetailsService;
import org.gnagoli.flowable.admin.client.idm.logic.properties.FlowableIdmAppProperties;
import org.gnagoli.flowable.admin.client.idm.security.AuthoritiesConstants;
import org.gnagoli.flowable.admin.client.idm.security.jwt.JWTConfigurer;
import org.gnagoli.flowable.admin.client.idm.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

/**
 * Based on http://docs.spring.io/spring-security/site/docs/3.2.x/reference/htmlsingle/#multiple-httpsecurity
 *
 * @author Joram Barrez
 * @author Tijs Rademakers
 * @author Filip Hrisafov
 * @author Luckmann GNAGOLI
 */
@Configuration(proxyBeanMethods = false)
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
//@ConditionalOnProperty(prefix = "flowable.idm.jwt", name = "enabled", havingValue = "false")
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
    // JWT AUTH
    //

    @Configuration
    @Order(SecurityConstants.IDM_API_SECURITY_ORDER)
    @Import(SecurityProblemSupport.class)
    public static class IdmApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        protected final FlowableRestAppProperties restAppProperties;
        protected final FlowableIdmAppProperties idmAppProperties;
        protected final ApiHttpSecurityCustomizer apiHttpSecurityCustomizer;
        protected final TokenProvider tokenProvider;
        protected final CorsFilter corsFilter;
        protected final SecurityProblemSupport problemSupport;

        public IdmApiWebSecurityConfigurationAdapter(FlowableRestAppProperties restAppProperties,
                                                     FlowableIdmAppProperties idmAppProperties, ApiHttpSecurityCustomizer apiHttpSecurityCustomizer, TokenProvider tokenProvider, CorsFilter corsFilter, SecurityProblemSupport problemSupport) {
            this.restAppProperties = restAppProperties;
            this.idmAppProperties = idmAppProperties;
            this.apiHttpSecurityCustomizer = apiHttpSecurityCustomizer;
            this.tokenProvider = tokenProvider;
            this.corsFilter = corsFilter;
            this.problemSupport = problemSupport;
        }

      /*  @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf()
                    .disable();

            if (idmAppProperties.isRestEnabled()) {

                if (restAppProperties.isVerifyRestApiPrivilege()) {
                    http.antMatcher("/api/idm/**")
                            .authorizeRequests()
                            .antMatchers("/api/idm/**")
                            .hasAuthority(DefaultPrivileges.ACCESS_REST_API);
                } else {
                    http.antMatcher("/api/idm/**").authorizeRequests().antMatchers("/api/idm/**").authenticated();

                }

                apiHttpSecurityCustomizer.customize(http);

            } else {
                http.antMatcher("/api/idm/**")
                        .authorizeRequests()
                        .antMatchers("/api/idm/**")
                        .denyAll();

            }

        }*/


        @Override
        public void configure(HttpSecurity http) throws Exception {


            // @formatter:off
            http
                    .csrf()
                    .disable()
                    .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling()
                    .authenticationEntryPoint(problemSupport)
                    .accessDeniedHandler(problemSupport)
                    .and()
                    .headers()
                    .contentSecurityPolicy("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' https://fonts.googleapis.com 'unsafe-inline'; img-src 'self' data:; font-src 'self' https://fonts.gstatic.com data:")
                    .and()
                    .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                    .and()
                    .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
                    .and()
                    .frameOptions()
                    .deny()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/api/authenticate").permitAll()
                    .antMatchers("/api/**").authenticated()
                    .antMatchers("/websocket/tracker").hasAuthority(AuthoritiesConstants.ADMIN)
                    .antMatchers("/websocket/**").permitAll()
                    .antMatchers("/management/health").permitAll()
                    .antMatchers("/management/info").permitAll()
                    .antMatchers("/management/prometheus").permitAll()
                    .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                    .and()
                    .httpBasic()
                    .and()
                    .apply(securityConfigurerAdapter());
            // @formatter:on
        }

        private JWTConfigurer securityConfigurerAdapter() {
            return new JWTConfigurer(tokenProvider);
        }


    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        return new CorsFilter(source);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
