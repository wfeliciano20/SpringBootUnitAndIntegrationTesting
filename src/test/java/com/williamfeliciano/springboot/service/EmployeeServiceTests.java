package com.williamfeliciano.springboot.service;

import com.williamfeliciano.springboot.exception.ResourceNotFoundException;
import com.williamfeliciano.springboot.model.Employee;
import com.williamfeliciano.springboot.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    public void setUp() {
        //employeeRepository = Mockito.mock(EmployeeRepository.class);
        //employeeService = new EmployeeService(employeeRepository);
        employee = Employee.builder()
                .id(1L)
                .firstName("William")
                .lastName("Feliciano")
                .email("wfeliciano@gmail.com")
                .build();
    }

    @DisplayName("Test for save employee operation")
    @Test
    public void givenEmployeeObj_whenSaveEmployee_thenReturnEmployeeObj() {
        // given precondition
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.empty());
        given(employeeRepository.save(employee)).willReturn(employee);
        // when action or behaviour
        Employee savedEmployee = employeeService.saveEmployee(employee);
        // then expected result
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0);
        assertThat(savedEmployee.getFirstName()).isEqualTo("William");
    }

    @DisplayName("Test for save employee operation which throws exception")
    @Test
    public void givenExistingEmployeeObj_whenSaveEmployee_thenThrowExcption() {
        // given precondition
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.of(employee));
        // given(employeeRepository.save(employee)).willThrow(new ResourceNotFoundException("Email already taken"));;
        // when action or behaviour
        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.saveEmployee(employee);
        });
        // then expected result
        verify(employeeRepository, never()).save(any(Employee.class));
    }


    @DisplayName("Test for find all employees operation")
    @Test
    public void givenListOfEmployee_whenGetAllEmployee_thenReturnsListOfEmployee() {
        // given precondition
        given(employeeRepository.findAll()).willReturn(List.of(employee));
        // when action or behaviour
        List<Employee> employeeList = employeeService.getAllEmployees();

        // then expected result
        assertThat(employeeList).isNotNull();
        assertThat(employeeList).hasSize(1);
        assertThat(employeeList.get(0).getFirstName()).isEqualTo("William");
    }

    @DisplayName("Test for find all employees operation when no employee is present")
    @Test
    public void givenEmptyListOfEmployee_whenGetAllEmployee_thenReturnsEmptyListOfEmployee() {
        // given precondition
        given(employeeRepository.findAll()).willReturn(Collections.emptyList());
        // when action or behaviour
        List<Employee> employeeList = employeeService.getAllEmployees();

        // then expected result
        assertThat(employeeList).isEmpty();
        assertThat(employeeList).hasSize(0);
    }

    @DisplayName("Test for find employee by id operation")

    @Test
    public void givenAnId_whenFindById_thenReturnEmployeeObj() {
        // given precondition
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));
        // when action or behaviour
        Optional<Employee> dbEmployee = employeeService.getEmployeeById(employee.getId());

        // then expected result
        assertThat(dbEmployee).isNotNull();
        assertThat(dbEmployee.get().getFirstName()).isEqualTo("William");
    }

    @DisplayName("Test for find employee by id operation when no employee is present")

    @Test
    public void givenInvalidId_whenFindById_thenReturnEmpty() {
        // given precondition
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.empty());
        // when action or behaviour
        Optional<Employee> dbEmployee = employeeService.getEmployeeById(employee.getId());

        // then expected result
        assertThat(dbEmployee).isEmpty();
    }

    @DisplayName("Test for updating employee operation")
    @Test
    public void givenAValidIDAndEmployeeObj_whenUpdateEmployee_thenReturnsModifiedEmployeeObj() {
        // given precondition
        given(employeeRepository.save(employee)).willReturn(employee);
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));
        Employee updatedEmployee = Employee.builder()
                .firstName("Will")
                .lastName("Felici")
                .email("wf@gmail.com")
                .build();

        // when action or behaviour
        Employee modifiedEmployee = employeeService.updateEmployee(employee.getId(), updatedEmployee);

        // then expected result
        assertThat(modifiedEmployee).isNotNull();
        assertThat(modifiedEmployee.getFirstName()).isEqualTo("Will");
        assertThat(modifiedEmployee.getLastName()).isEqualTo("Felici");
    }

    @DisplayName("Test for updating employee operation when invalid id is given and no employee is found")
    @Test
    public void givenAnInValidIDAndEmployeeObj_whenUpdateEmployee_thenThrowsExcepion() {
        // given precondition
        given(employeeRepository.findById(employee.getId())).willThrow(new ResourceNotFoundException("Employee not found"));
        Employee updatedEmployee = Employee.builder()
                .firstName("Will")
                .lastName("Felici")
                .email("wf@gmail.com")
                .build();

        // when action or behaviour
        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(employee.getId(), updatedEmployee));

        // then expected result
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @DisplayName("Test for deleting employee operation")
    @Test
    public void givenAValidID_whenDeleteEmployee_thenReturnsDeletedEmployeeObj() {
        // given precondition
        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));
        willDoNothing().given(employeeRepository).deleteById(employee.getId());
        // when action or behaviour
        Employee deletedEmployee = employeeService.deleteEmployee(employee.getId());
        // then expected result
        assertThat(deletedEmployee).isNotNull();
        assertThat(deletedEmployee.getFirstName()).isEqualTo("William");
        verify(employeeRepository, times(1)).deleteById(employee.getId());
    }

    @DisplayName("Test for deleting employee operation when invalid id is given and no employee is found")
    @Test
    public void givenAnInValidID_whenDeleteEmployee_thenThrowsExcepion() {
        // given precondition
        given(employeeRepository.findById(employee.getId())).willThrow(new ResourceNotFoundException("Employee not found"));
        // when action or behaviour
        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(employee.getId()));
        // then expected result
        verify(employeeRepository, never()).deleteById(employee.getId());
    }
}
