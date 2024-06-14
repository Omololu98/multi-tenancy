package dev.iyanu.multitenancy.service;

import dev.iyanu.multitenancy.entities.Employee;
import dev.iyanu.multitenancy.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;


    public String createEmployee(Employee employee) {
        Employee newEmployee = new Employee();
        newEmployee.setEmail(employee.getEmail());
        newEmployee.setName(employee.getName());
        employeeRepository.save(newEmployee);
        return "Employee saved successfully";
    }

}
