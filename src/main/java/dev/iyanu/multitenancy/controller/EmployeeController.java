package dev.iyanu.multitenancy.controller;

import dev.iyanu.multitenancy.entities.Employee;
import dev.iyanu.multitenancy.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/")
    public String addEmployee(@RequestBody Employee employee){
        return employeeService.createEmployee(employee);
    }
}
