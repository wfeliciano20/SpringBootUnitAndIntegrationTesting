package com.williamfeliciano.springboot.repository;

import com.williamfeliciano.springboot.model.Employee;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;


@DataJpaTest
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    public void setup() {
        employee = Employee.builder()
                .firstName("William")
                .lastName("Feliciano")
                .email("williamF@gmail.com")
                .build();
    }

    @DisplayName("Junit test for save employee operation")
    @Test
    public void givenEmployeeObject_whenSaved_thenReturnSavedEmployee() {
        // given Before Each Employee

        // when
        Employee savedEmployee = employeeRepository.save(employee);

        // then
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0);
        assertThat(savedEmployee.getFirstName()).isEqualTo("William");
    }

    @DisplayName("Junit test for find all employees operation")
    @Test
    public void givenManyEmployees_whenFindAll_thenEmployeeList() {
        // given precondition Before Each Employee

        Employee employee2 = Employee.builder()
                .firstName("John")
                .lastName("Cena")
                .email("cena@gmail.com")
                .build();
        employeeRepository.save(employee);
        employeeRepository.save(employee2);
        // when action or behaviour
        List<Employee> employeeList = employeeRepository.findAll();

        // then expected result
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).hasSize(2);
        assertThat(employeeList.get(0).getFirstName()).isEqualTo("William");
        assertThat(employeeList.get(1).getFirstName()).isEqualTo("John");
    }

    @DisplayName("Junit test for find employee by id operation")
    @Test
    public void givenEmployeeId_whenFindById_thenEmployeeObj() {
        // given Before Each Employee

        Employee savedEmployee = employeeRepository.save(employee);
        // when action or behaviour
        Employee dbEmployee = employeeRepository.findById(savedEmployee.getId()).get();
        // then expected result
        assertThat(dbEmployee).isNotNull();
        assertThat(dbEmployee.getFirstName()).isEqualTo("William");
    }

    @DisplayName("Junit test for find employee by email operation")
    @Test
    public void givenEmployeeEmail_whenFindByEmail_thenEmployeeObj() {
        // given Before Each Employee

        Employee savedEmployee = employeeRepository.save(employee);
        // when action or behaviour
        Employee dbEmployee = employeeRepository.findByEmail(savedEmployee.getEmail()).get();

        // then expected result
        assertThat(dbEmployee).isNotNull();
        assertThat(dbEmployee.getFirstName()).isEqualTo("William");
        assertThat(dbEmployee.getEmail()).isEqualTo("williamF@gmail.com");
    }

    @DisplayName("Junit test for update employee operation")
    @Test
    public void givenSavedEmployeeObj_whenUpdated_thenReturnUpdatedObj() {
        // given Before Each Employee

        employeeRepository.save(employee);
        // when action or behaviour
        Employee dbEmployee = employeeRepository.findByEmail("williamF@gmail.com").get();
        dbEmployee.setFirstName("Will");
        dbEmployee.setLastName("Felici");
        dbEmployee.setEmail("wF@gmail.com");
        Employee updatedEmployee = employeeRepository.save(dbEmployee);
        // then expected result

        assertThat(updatedEmployee.getFirstName()).isEqualTo("Will");
        assertThat(updatedEmployee.getLastName()).isEqualTo("Felici");
        assertThat(updatedEmployee.getEmail()).isEqualTo("wF@gmail.com");
    }

    @DisplayName("Junit test for delete employee operation")
    @Test
    public void givenSavedEmployee_whenDelete_thenRemoveEmployee() {
        // given  Before Each Employee

        employeeRepository.save(employee);
        // when action or behaviour
        employeeRepository.delete(employee);

        Optional<Employee> deletedEmployee = employeeRepository.findByEmail("williamF@gamil.com");

        // then expected result
        assertThat(deletedEmployee).isEmpty();
    }

}
