package dev.iyanu.multitenancy.service;

import dev.iyanu.multitenancy.tenant.repository.UserRepository;
import dev.iyanu.multitenancy.tenant.users.Tenants;
import dev.iyanu.multitenancy.tenant.repository.TenantRepository;
import dev.iyanu.multitenancy.security_config.TenantManager;
import dev.iyanu.multitenancy.tenant.users.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantManager tenantManager;
    private final UserRepository userRepository;


    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    @Transactional
    public ResponseEntity<String> register(Tenants tenants) throws SQLException {
        Tenants newTenant = new Tenants();
        newTenant.setEmail(tenants.getEmail());
        newTenant.setName(tenants.getName());
        newTenant.setTenantId();
        newTenant.setDatabaseName(StringUtils.substringBefore(tenants.getName()," "));
        tenantRepository.save(newTenant);
        tenantManager.addTenant(newTenant);
        return ResponseEntity.ok().body("Tenant Registered successfully: Tenant ID is "+newTenant.getTenantId());

    }

    public List<Tenants> findAllTenant() {
        return tenantRepository.findAll();
    }
}
