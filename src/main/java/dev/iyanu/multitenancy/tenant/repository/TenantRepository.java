package dev.iyanu.multitenancy.tenant.repository;

import dev.iyanu.multitenancy.tenant.users.Tenants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenants, Integer> {

    Optional<Tenants> findByTenantId(String tenantId);
}
