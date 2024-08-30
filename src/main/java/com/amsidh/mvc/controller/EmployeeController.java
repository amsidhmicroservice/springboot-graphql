package com.amsidh.mvc.controller;

import com.amsidh.mvc.controller.request.SaveEmployee;
import com.amsidh.mvc.entity.Employee;
import com.amsidh.mvc.graphql.filters.FilterCriteria;
import com.amsidh.mvc.graphql.filters.SortBy;
import com.amsidh.mvc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    @QueryMapping
    public List<? extends Employee> searchEmployee(@Argument List<FilterCriteria> filterCriteria,
                                         @Argument int offset,
                                         @Argument int limit,
                                         @Argument SortBy sortBy) {
        return employeeService.findEmployees(filterCriteria, offset, limit, List.of(sortBy));
    }

    @MutationMapping
    public Employee saveEmployee(@Argument(name = "saveEmployee") SaveEmployee saveEmployee) {
        Employee employee = Employee.builder().name(saveEmployee.getName()).emailId(saveEmployee.getEmailId()).build();
        return employeeService.saveEmployee(employee);
    }
}
