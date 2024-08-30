package com.amsidh.mvc.service;

import com.amsidh.mvc.graphql.filters.*;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DynamicQueryBuilder<T> {

    public Specification<T> buildSpecification(List<FilterCriteria> filterCriteriaList, SortBy sortBy) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (FilterCriteria filterCriteria : filterCriteriaList) {
                String key = filterCriteria.getKey();
                FilterInput filterInput = filterCriteria.getFilterInput();
                FilterType filterType = filterInput.getFilterType();
                List<String> filterValues = filterInput.getFilterValues();

                Path<?> path = root.get(key);
                Class<?> fieldType = path.getJavaType();

                switch (filterType) {
                    case eq:
                        List<Object> eqValues = filterValues.stream()
                                .map(value -> convertToType(fieldType, value))
                                .collect(Collectors.toList());
                        predicates.add(path.in(eqValues));
                        break;

                    case nq:
                        List<Object> nqValues = filterValues.stream()
                                .map(value -> convertToType(fieldType, value))
                                .collect(Collectors.toList());
                        predicates.add(criteriaBuilder.not(path.in(nqValues)));
                        break;

                    case contains:
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                                "%" + filterValues.get(0).toLowerCase() + "%"));
                        break;

                    case notContains:
                        predicates.add(criteriaBuilder.not(criteriaBuilder.like(criteriaBuilder.lower(path.as(String.class)),
                                "%" + filterValues.get(0).toLowerCase() + "%")));
                        break;

                    case gte:
                        if (Comparable.class.isAssignableFrom(fieldType)) {
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                    castPathToComparable(path, fieldType),
                                    (Comparable) convertToType(fieldType, filterValues.get(0))
                            ));
                        } else {
                            throw new IllegalArgumentException("Field type does not support comparison: " + fieldType);
                        }
                        break;

                    case lte:
                        if (Comparable.class.isAssignableFrom(fieldType)) {
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                    castPathToComparable(path, fieldType),
                                    (Comparable) convertToType(fieldType, filterValues.get(0))
                            ));
                        } else {
                            throw new IllegalArgumentException("Field type does not support comparison: " + fieldType);
                        }
                        break;

                    case eqIgnoreCase:
                        predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(path.as(String.class)),
                                filterValues.get(0).toLowerCase()));
                        break;

                    default:
                        throw new IllegalArgumentException("Unknown filter type: " + filterType);
                }
            }

            query.where(predicates.toArray(new Predicate[0]));

            if (sortBy != null) {
                query.orderBy(sortBy.getOrder() == SortOrder.ASC ?
                        criteriaBuilder.asc(root.get(sortBy.getKey())) :
                        criteriaBuilder.desc(root.get(sortBy.getKey())));
            }

            return query.getRestriction();
        };
    }

    private Object convertToType(Class<?> fieldType, String value) {
        if (fieldType == Integer.class) {
            return Integer.parseInt(value);
        } else if (fieldType == Long.class) {
            return Long.parseLong(value);
        } else if (fieldType == Double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == Float.class) {
            return Float.parseFloat(value);
        } else if (fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value; // Default to String
    }

    @SuppressWarnings("unchecked")
    private <Y extends Comparable<? super Y>> Expression<Y> castPathToComparable(Path<?> path, Class<?> fieldType) {
        return (Expression<Y>) path.as((Class<Y>) fieldType);
    }
}
