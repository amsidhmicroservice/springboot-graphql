package com.amsidh.mvc.graphql.resolver.query;

import com.amsidh.mvc.entity.Employee;
import com.amsidh.mvc.graphql.filters.FilterCriteria;
import com.amsidh.mvc.graphql.filters.SortBy;
import com.amsidh.mvc.graphql.resolver.MasterDataResolver;
import com.amsidh.mvc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
public class SearchEmployeeResolver implements MasterDataResolver {

    private final EmployeeRepository employeeRepository;

    @QueryMapping
    public List<Employee> searchEmployee(@Argument(name = "filterCriteria") List<FilterCriteria> filterCriteria,
                                         @Argument(name = "") int offset,
                                         @Argument(name = "limit") int limit,
                                         @Argument(name = "sortBy") SortBy sortBy) {
        return getData(employeeRepository, filterCriteria, offset, limit, "employee", Employee.class, sortBy);
    }
}
