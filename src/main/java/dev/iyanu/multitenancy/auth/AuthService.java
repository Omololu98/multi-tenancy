package dev.iyanu.multitenancy.auth;


import dev.iyanu.multitenancy.entities.Employee;
import dev.iyanu.multitenancy.tenant.users.Tenants;
import dev.iyanu.multitenancy.repository.EmployeeRepository;
import dev.iyanu.multitenancy.tenant.repository.TenantRepository;
import dev.iyanu.multitenancy.tenant.repository.UserRepository;
import dev.iyanu.multitenancy.security_config.JwtService;
import dev.iyanu.multitenancy.security_config.TenantManager;
import dev.iyanu.multitenancy.tenant.users.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final TenantRepository tenantRepository;
    private final TenantManager tenantManager;


    public ResponseEntity<String> registerUser(RegisterRequest registerRequest) {

        //note that no user will be registered if the tenantId is not passed or is not correct
       Optional<Tenants> tenants = tenantRepository.findByTenantId(registerRequest.getTenantId());
       if(tenants.isEmpty()){
           throw new IllegalArgumentException("Tenant Id provided is not correct");
       }
        User  user = User.builder()
                .email(registerRequest.getEmail())
                .name(registerRequest.getName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .tenantId(registerRequest.getTenantId())
                .build();
        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok().body("user registered successfully");
    }


    public ResponseEntity<AuthenticationResponse> loginUser(AuthenticateRequest authenticateRequest) {
            //authenticate the User
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authenticateRequest.getEmail(),authenticateRequest.getPassword()));

            //this line of code runs oly if authentication is successful
            User user = userRepository.findByEmail(authenticateRequest.getEmail()).orElseThrow();
            String jwt = jwtService.generateToken(user);

            //set the security context to allow routing of db and to know the tenantID and also set the datasource

            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()));
            tenantManager.setCurrentTenant();
        System.out.println("I have set my current tenant to "+tenantManager.getCurrentTenant());
        //create an Employee Object for the user once
        Employee employee =  employeeRepository.findByEmail(user.getEmail());
        if(employee==null){
            Employee newEmployee = new Employee();
            newEmployee.setName(user.getName());
            newEmployee.setEmail(user.getEmail());
            employeeRepository.save(newEmployee);
        }
        System.out.println("Employee "+employee.getName()+" is logged in");
        return ResponseEntity.ok().body(new AuthenticationResponse(jwt));
    }




}
