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

import org.gnagoli.flowable.admin.client.common.service.idm.RemoteIdmServiceImpl;
import org.gnagoli.flowable.admin.client.idm.config.servlet.ApiDispatcherServletConfiguration;
import org.gnagoli.flowable.admin.client.idm.config.servlet.AppDispatcherServletConfiguration;
import org.gnagoli.flowable.admin.client.idm.logic.properties.FlowableIdmAppProperties;
import org.flowable.spring.boot.FlowableSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FlowableIdmAppProperties.class)
@AutoConfigureBefore({
        FlowableSecurityAutoConfiguration.class,
})
@ComponentScan(basePackages = {
    "org.gnagoli.flowable.admin.client.idm.config.conf",
    "org.gnagoli.flowable.admin.client.idm.config.security",
    "org.gnagoli.flowable.admin.client.idm.logic.service"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = RemoteIdmServiceImpl.class)})
public class ApplicationConfiguration {

    @Bean
    public ServletRegistrationBean<DispatcherServlet> idmApiServlet(ApplicationContext applicationContext) {
        AnnotationConfigWebApplicationContext dispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
        dispatcherServletConfiguration.setParent(applicationContext);
        dispatcherServletConfiguration.register(ApiDispatcherServletConfiguration.class);
        DispatcherServlet servlet = new DispatcherServlet(dispatcherServletConfiguration);
        ServletRegistrationBean<DispatcherServlet> registrationBean = new ServletRegistrationBean<>(servlet, "/api/idm/*");
        registrationBean.setName("Flowable IDM App API Servlet");
        registrationBean.setLoadOnStartup(1);
        registrationBean.setAsyncSupported(true);
        return registrationBean;
    }



    @Bean
    public ServletRegistrationBean<DispatcherServlet> idmAppServlet(ApplicationContext applicationContext) {
        AnnotationConfigWebApplicationContext dispatcherServletConfiguration = new AnnotationConfigWebApplicationContext();
        dispatcherServletConfiguration.setParent(applicationContext);
        dispatcherServletConfiguration.register(AppDispatcherServletConfiguration.class);
        DispatcherServlet servlet = new DispatcherServlet(dispatcherServletConfiguration);
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet, "/idm-app/*");
        registrationBean.setName("Flowable IDM App Servlet");
        registrationBean.setLoadOnStartup(1);
        registrationBean.setAsyncSupported(true);
        return registrationBean;
    }

//    @Bean
//    public WebMvcConfigurer idmApplicationWebMvcConfigurer() {
//        return new WebMvcConfigurer() {
//
//            @Override
//            public void addViewControllers(@NonNull ViewControllerRegistry registry) {
//
//                if (!ClassUtils.isPresent("org.flowable.ui.task.conf.ApplicationConfiguration", getClass().getClassLoader())) {
//                    // If the task application is not present, then the root should be mapped to admin
//                    registry.addViewController("/").setViewName("redirect:/idm/");
//                }
//                registry.addViewController("/idm").setViewName("redirect:/idm/");
//                registry.addViewController("/idm/").setViewName("forward:/idm/index.html");
//            }
//        };
//    }


}
