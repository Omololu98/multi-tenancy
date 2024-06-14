package dev.iyanu.multitenancy.config;
import dev.iyanu.multitenancy.security_config.TenantManager;
import dev.iyanu.multitenancy.tenant.repository.TenantRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;


@Configuration
public class TenantManagerConfig {
    private final TenantRepository tenantRepository;

    public TenantManagerConfig(TenantRepository tenantRepository, TenantManager tenantManager) {
        tenantManager.setTenantResolver(this::tenantResolver);
        this.tenantRepository = tenantRepository;
    }

    private DataSource tenantResolver(String tenantId){
        return tenantRepository.findByTenantId(tenantId).map(tenants -> {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setPassword("Josephomololu98$");
            dataSource.setUsername("root");
            dataSource.setUrl("jdbc:mysql://localhost:3306/"+tenants.getDatabaseName());
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            return dataSource;
        }).orElseThrow(()-> new IllegalArgumentException("Error occurred with resolving datasource for tenant id: "+tenantId));
    }

}
