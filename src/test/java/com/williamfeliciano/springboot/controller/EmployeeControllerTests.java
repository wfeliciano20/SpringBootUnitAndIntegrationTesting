package com.williamfeliciano.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.williamfeliciano.springboot.exception.ResourceNotFoundException;
import com.williamfeliciano.springboot.model.Employee;
import com.williamfeliciano.springboot.service.EmployeeService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTests {

    private static Employee employee;
    private static List<Employee> employeeList;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private EmployeeService employeeService;

    @BeforeEach
    public void setup() {
        employee = Employee.builder()
                .firstName("William")
                .lastName("Feliciano")
                .email("wf@gmail.com")
                .build();
        Employee employee1 = Employee.builder()
                .firstName("John")
                .lastName("Cena")
                .email("cena@gmail.com")
                .build();
        employeeList = new ArrayList<>(List.of(employee, employee1));
    }

    @Test
    public void givenEmployeeObj_whenSaveEmployee_returnSavedEmployee() throws Exception {
        // Mock SaveEmployee service call
        given(employeeService.saveEmployee(any(Employee.class))).willAnswer(invocation -> invocation.getArgument(0));
        // mock the post request
        ResultActions response = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));
        // verify the response
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
                .andExpect(jsonPath("$.email", is(employee.getEmail())));
    }

    @Test
    public void givenAListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {
        // given precondition
        given(employeeService.getAllEmployees()).willReturn(employeeList);
        // when action or behaviour
        ResultActions response = mockMvc.perform(get("/api/employees"));
        // then expected result
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(employeeList.size())))
                .andExpect(jsonPath("$[0].firstName", is(employee.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(employee.getLastName())))
                .andExpect(jsonPath("$[0].email", is(employee.getEmail())))
                .andExpect(jsonPath("$[1].firstName", is(employeeList.get(1).getFirstName())))
                .andExpect(jsonPath("$[1].lastName", is(employeeList.get(1).getLastName())))
                .andExpect(jsonPath("$[1].email", is(employeeList.get(1).getEmail())));
    }

    @Test
    public void givenEmployeeId_whenGetEmployeeId_thenEmployeeObj() throws Exception {
        // given precondition
        long employeeId = 1L;
        given(employeeService.getEmployeeById(1L)).willReturn(java.util.Optional.of(employee));
        // when action or behaviour
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));
        // then expected result
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())));
    }

    @Test
    public void givenInvalidEmployeeId_whenGetEmployeeId_thenReturnException() throws Exception {
        // given precondition
        long invalidEmployeeId = 5L;
        given(employeeService.getEmployeeById(invalidEmployeeId)).willReturn(Optional.empty());
        // when action or behaviour
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", invalidEmployeeId));

        // then expected result
        response.andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenAValidIdAndUpdateObj_whenUpdateObj_thenReturnEmployeeObj() throws Exception {
        // given precondition
        long employeeId = 1L;
        Employee employeeUpdate = Employee.builder()
                .firstName("Will")
                .lastName("Felici")
                .email("wFelici@gmail.com")
                .build();
        given(employeeService.updateEmployee(employeeId, employeeUpdate)).willReturn(employeeUpdate);
        // when action or behaviour
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeUpdate)));
        // then expected result
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employeeUpdate.getId())))
                .andExpect(jsonPath("$.firstName", is(employeeUpdate.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(employeeUpdate.getLastName())))
                .andExpect(jsonPath("$.email", is(employeeUpdate.getEmail())));
    }

    @Test()
    public void givenInvalidEmployeeId_whenUpdateEmployee_thenThrowException() throws Exception {
        // given precondition
        long invalidEmployeeId = 5L;
        Employee employeeUpdate = Employee.builder()
                .firstName("Will")
                .lastName("Felici")
                .email("wfelici@gmail.com")
                .build();
        given(employeeService.updateEmployee(invalidEmployeeId, employeeUpdate)).willThrow(new ResourceNotFoundException("Employee not found"));
        // do the request and verify the response
        Assertions.assertThatThrownBy(() ->
                        mockMvc.perform(put("/api/employees/{id}", invalidEmployeeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(employeeUpdate))))
                .hasCauseInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found");
    }


    @Test
    public void givenValidId_whenDeleteEmployee_thenReturnsDeletedEmployee() throws Exception {
        // given precondition
        long employeeId = 1L;
        given(employeeService.deleteEmployee(employeeId)).willReturn(employee);
        // when action or behaviour
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employeeId));

        // then expected result
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())));
    }

    @Test
    public void givenInvalidId_whenDeleteEmployee_thenThrowsException() {
        // given precondition
        long invalidEmployeeId = 5L;
        given(employeeService.deleteEmployee(invalidEmployeeId)).willThrow(new ResourceNotFoundException("Employee not found"));
        // when action or behaviour
        Assertions.assertThatThrownBy(() -> mockMvc.perform(delete("/api/employees/{id}", invalidEmployeeId)))
                .hasCauseInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found");


    }


}


