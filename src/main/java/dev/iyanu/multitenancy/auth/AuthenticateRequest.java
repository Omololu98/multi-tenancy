package dev.iyanu.multitenancy.auth;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthenticateRequest {

    private String email;
    private String password;
}
