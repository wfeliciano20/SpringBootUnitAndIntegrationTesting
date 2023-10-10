package com.williamfeliciano.springboot.service;

import com.williamfeliciano.springboot.exception.ResourceNotFoundException;
import com.williamfeliciano.springboot.model.Employee;
import com.williamfeliciano.springboot.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;


    public Employee saveEmployee(Employee employee) {

        // Verify if the email already exists
        Optional<Employee> savedEmployee = employeeRepository.findByEmail(employee.getEmail());
        if (savedEmployee.isPresent()) {
            throw new ResourceNotFoundException("Email already taken");
        }
        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee updateEmployee(Long id, Employee employee) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found");
        }
        Employee employeeFromDB = employeeOptional.get();
        employeeFromDB.setFirstName(employee.getFirstName());
        employeeFromDB.setLastName(employee.getLastName());
        employeeFromDB.setEmail(employee.getEmail());
        return employeeRepository.save(employeeFromDB);
    }

    public Employee deleteEmployee(Long id){
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found");
        }
        Employee employeeFromDB = employeeOptional.get();
        employeeRepository.deleteById(id);
        return employeeFromDB;
    }
}
