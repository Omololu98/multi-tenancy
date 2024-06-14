package dev.iyanu.multitenancy.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@EnableJpaRepositories(basePackages = "dev.iyanu.multitenancy.tenant.repository",
                        entityManagerFactoryRef = "adminEntityManager",
                        transactionManagerRef = "adminTransactionManager")
public class AdminDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.admin")
    public DataSourceProperties adminDataSourceProperties(){
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.admin.configuration")
    public DataSource adminDataSource(){
        System.out.println("Admin datasource is live");
        return adminDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "adminEntityManager")
    public LocalContainerEntityManagerFactoryBean adminEntityManager(EntityManagerFactoryBuilder builder){
            return builder.dataSource(adminDataSource())
                    .packages("dev.iyanu.multitenancy.tenant.users")
                    .build();
    }

    @Bean(name = "adminTransactionManager")
    public PlatformTransactionManager adminTransactionManager(final @Qualifier("adminEntityManager")
                                                                  LocalContainerEntityManagerFactoryBean bean){
        return new JpaTransactionManager(bean.getObject());
    }
}
