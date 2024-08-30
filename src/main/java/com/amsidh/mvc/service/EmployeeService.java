package com.amsidh.mvc.service;

import com.amsidh.mvc.entity.Employee;
import com.amsidh.mvc.graphql.filters.FilterCriteria;
import com.amsidh.mvc.graphql.filters.SortBy;
import com.amsidh.mvc.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> findEmployees(List<FilterCriteria> filterCriteria, int offset, int limit, List<SortBy> sortBy) {
        DynamicQueryBuilder<Employee> dynamicQueryBuilder = new DynamicQueryBuilder<>();
        Specification<Employee> specification = dynamicQueryBuilder.buildSpecification(filterCriteria, sortBy != null && !sortBy.isEmpty() ? sortBy.get(0) : null);

        Pageable pageable = PageRequest.of(offset, limit);
        Page<Employee> employeePage = employeeRepository.findAll(specification, pageable);

        return employeePage.getContent();
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
}
