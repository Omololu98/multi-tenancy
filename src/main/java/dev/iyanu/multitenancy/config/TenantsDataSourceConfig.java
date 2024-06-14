package dev.iyanu.multitenancy.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "dev.iyanu.multitenancy.repository",
        entityManagerFactoryRef = "tenantEntityManager",
        transactionManagerRef = "tenantTransactionManager")
public class TenantsDataSourceConfig {


    @Bean(name = "tenantEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean tenantEntityManager(EntityManagerFactoryBuilder builder, DataSource dataSource){
        return builder.dataSource(dataSource)
                .packages("dev.iyanu.multitenancy.entities")
                .build();
    }


    @Bean(name = "tenantTransactionManager")
    @Primary
    public PlatformTransactionManager tenantTransactionManager(final @Qualifier("tenantEntityManager")
                                                              LocalContainerEntityManagerFactoryBean bean){
        return new JpaTransactionManager(bean.getObject());
    }

}
