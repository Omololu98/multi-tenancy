package dev.iyanu.multitenancy.security_config;

import com.zaxxer.hikari.HikariDataSource;
import dev.iyanu.multitenancy.tenant.users.Tenants;
import dev.iyanu.multitenancy.tenant.users.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Configuration
@Slf4j
public class TenantManager {

    //basically this function creates a datasource and does nothing else
    private Function<String,DataSource> tenantResolver;
    private final Map<Object,Object> tenantDataSources = new ConcurrentHashMap<>();
    private final ThreadLocal<Object> currentTenant= new ThreadLocal<>();
    private AbstractRoutingDataSource dataRouter;
    private DataSource dataSource;

    @Bean
    @Primary
    public DataSource dataSource(){
        dataRouter = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                if(currentTenant.get() == null){
                    return null;
                }
                return currentTenant.get();
            }
        };
        dataSource = defaultDataSource();
        dataRouter.setTargetDataSources(tenantDataSources);
        dataRouter.setDefaultTargetDataSource(dataSource);
        dataRouter.afterPropertiesSet();
        return dataRouter;
    }


    public void setTenantResolver(Function<String,DataSource> tenantResolver){
        this.tenantResolver = tenantResolver;
    }

    public void setCurrentTenant(){
        String tenantId = null;
        var authToken = SecurityContextHolder.getContext().getAuthentication();
        if(authToken!= null && authToken.getPrincipal() instanceof User user){
             tenantId = user.getTenantId();
        }
        if(!isTenantLoaded(tenantId)){
            if(tenantResolver!=null){
                DataSource newDataSource;
                newDataSource = tenantResolver.apply(tenantId); //you need to provide this implementation in another class
                tenantDataSources.put(tenantId,newDataSource);
                dataRouter.afterPropertiesSet(); //update the local storage that stores my dataSources
            }
        }
        currentTenant.set(tenantId);
    }

    public Object getCurrentTenant(){
        return currentTenant.get();
    }

    public void addTenant(Tenants tenants) throws SQLException {
        //create a db for the tenant
        createDatabase(tenants);

        //create dataSource for the new tenant
       DataSource newDataSource = tenantResolver.apply(tenants.getTenantId());

       //test and log the connection
        try(Connection c = newDataSource.getConnection()){
            tenantDataSources.put(tenants.getTenantId(),newDataSource);
            dataRouter.afterPropertiesSet();
            System.out.println("The new datasource is live");
        }
    }


    private boolean isTenantLoaded(String tenantId){
        return tenantDataSources.containsKey(tenantId);
    }


    private void createDatabase(Tenants tenants){
        try(Connection connection = defaultDataSource().getConnection()){
            var databaseName = StringUtils.substringBefore(tenants.getName()," ");
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE "+databaseName);
            log.info("Database for {} created successfully", tenants.getName());
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    private DataSource defaultDataSource(){
        return dataSourceBuilder("tenanant");
    }


    private static DataSource dataSourceBuilder(String databaseName){
        var properties = new DataSourceProperties();
        properties.setPassword("Josephomololu98$");
        properties.setUsername("root");
        properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        properties.setUrl("jdbc:mysql://localhost:3306/"+databaseName);
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }


}
