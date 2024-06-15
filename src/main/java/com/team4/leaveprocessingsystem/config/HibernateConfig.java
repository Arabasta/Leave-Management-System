// For Database meta-information related features - https://stackoverflow.com/questions/51501405/how-to-get-the-all-table-metadata-in-spring-boot-jpa-hibernate

package com.team4.leaveprocessingsystem.config;

import com.team4.leaveprocessingsystem.integrators.MetadataExtractorIntegrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class HibernateConfig implements HibernatePropertiesCustomizer {

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.integrator_provider",
                (IntegratorProvider) () -> Collections.singletonList(MetadataExtractorIntegrator.INSTANCE));
    }
}
