package dev.iyanu.multitenancy.controller;

import dev.iyanu.multitenancy.tenant.users.Tenants;
import dev.iyanu.multitenancy.service.TenantService;
import dev.iyanu.multitenancy.tenant.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenant")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/register")
    public ResponseEntity<String> registerTenant(@RequestBody Tenants tenants) throws SQLException {
        return tenantService.register(tenants);
    }

    @GetMapping("/")
    public List<Tenants> getAll() {
        return tenantService.findAllTenant();
    }

    @GetMapping("/users")
    public List<User> getAllUsers(){
        return tenantService.findAllUsers();
    }

}
