package com.amsidh.mvc.repository;

import com.amsidh.mvc.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

    public static Specification<Employee> hasPropertyEqualTo(String propertyName, Object value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(propertyName), value);
    }

    public static Specification<Employee> hasPropertyLike(String propertyName, String value) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(propertyName), "%" + value + "%");
    }

    // Additional specifications for other filter types
}
