package dev.iyanu.multitenancy.tenant.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tenants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String email;
    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "database_name")
    private String databaseName;

    public void setTenantId() {
        this.tenantId = StringUtils.substringBefore(UUID.randomUUID().toString(),"-");
    }
}
