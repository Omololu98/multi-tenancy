package dev.iyanu.multitenancy.auth;


import dev.iyanu.multitenancy.tenant.users.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String tenantId;
    private Role role;

}
