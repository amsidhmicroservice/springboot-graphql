package com.amsidh.mvc.graphql.resolver.mutation;

import com.amsidh.mvc.entity.Employee;
import com.amsidh.mvc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SaveEmployeeResolver {

    private final EmployeeRepository employeeRepository;

    @MutationMapping
    public Employee saveEmployee(@Argument(name = "saveEmployee") SaveEmployee saveEmployee) {
        Employee employee = Employee.builder().name(saveEmployee.getName()).emailId(saveEmployee.getEmailId()).build();
        return employeeRepository.save(employee);
    }
}
