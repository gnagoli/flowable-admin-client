package org.gnagoli.flowable.admin.client;

import org.gnagoli.flowable.admin.client.admin.logic.properties.FlowableAdminAppProperties;
import org.gnagoli.flowable.admin.client.common.properties.FlowableCommonAppProperties;
import org.gnagoli.flowable.admin.client.common.properties.FlowableRestAppProperties;
import org.gnagoli.flowable.admin.client.idm.logic.properties.FlowableIdmAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {
        FlowableAdminAppProperties.class,
        FlowableCommonAppProperties.class,
        FlowableRestAppProperties.class,
        FlowableIdmAppProperties.class
})
public class FlowableStaterApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowableStaterApiApplication.class, args);
    }

}
