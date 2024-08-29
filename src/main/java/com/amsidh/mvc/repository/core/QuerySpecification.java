package com.amsidh.mvc.repository.core;

import com.amsidh.mvc.graphql.filters.FilterCriteria;
import com.amsidh.mvc.graphql.filters.FilterType;
import com.amsidh.mvc.graphql.filters.SortBy;
import com.amsidh.mvc.utils.TypeConvertor;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class QuerySpecification {
    private static final String NUMERIC_REGEX = "[0-9]+[.]?[0-9]*";

    private QuerySpecification() {
    }

    private static final Function<String, List<String>> SPLIT_FUNCTION = key -> Arrays.asList(key.split("\\."));

    public static <R> Specification<R> getSpecification(List<FilterCriteria> filterCriteriaList, SortBy sortBy, boolean isDistinctQuery) {
        return ((root, query, criteriaBuilder) -> {
            if (Objects.nonNull(filterCriteriaList) && !filterCriteriaList.isEmpty()) {
                final List<Predicate> predicates = filterCriteriaList.stream().map(c -> getPredicate(c, root, criteriaBuilder)).collect(Collectors.toList());
                getSpecificationInfo(filterCriteriaList, query, criteriaBuilder, predicates, isDistinctQuery);
            }
            getOrderBy(sortBy, root, query, criteriaBuilder);
            return null;
        });
    }

    private static <R> void getOrderBy(SortBy sortBy, Root<R> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (Objects.nonNull(sortBy)) {
            query.orderBy(sortBy.getOrder().name().equalsIgnoreCase("DESC") ? criteriaBuilder.desc(root.get(sortBy.getKey())) : criteriaBuilder.asc(root.get(sortBy.getKey())));
        }
    }

    private static void getSpecificationInfo(List<FilterCriteria> filterCriteriaList, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, boolean isDistinctQuery) {
        if (!filterCriteriaList.stream().filter(filterCriteria -> filterCriteria.getKey().contains(".")).collect(Collectors.toList()).isEmpty() && isDistinctQuery) {
            query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0]))).distinct(true);
        } else {
            query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }
    }

    private static Predicate getPredicate(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder) {
        final Class<?> keyType = BI_FUNCTION.apply(filterCriteria, root);
        final FilterType filterType = filterCriteria.getFilterInput().getFilterType();
        final List<?> values = TypeConvertor.mapDataToType(keyType, filterCriteria.getFilterInput());
        assert values != null;
        final List<?> nonNulValues = values.stream().filter(Objects::nonNull).toList();
        return getPredicateWithEqOrNq(filterCriteria, root, criteriaBuilder, null, filterType, values, nonNulValues, keyType);

    }

    private static Predicate getPredicate(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, List<?> values, List<?> nonNullValues) {
        if (filterCriteria.getKey().contains(".")) {
            final List<String> filterKeys = SPLIT_FUNCTION.apply(filterCriteria.getKey());
            return criteriaBuilder.not(root.join(filterKeys.get(0)).get(filterKeys.get(1)).in(nonNullValues));
        } else {
            final Predicate predicate1 = criteriaBuilder.not(root.get(filterCriteria.getKey()).in(nonNullValues));
            final Predicate predicate2 = root.get(filterCriteria.getKey()).isNotNull();
            final Predicate predicate3 = root.get(filterCriteria.getKey()).isNull();
            return values.contains(null) ? nonNullValues.isEmpty() ? predicate2 : criteriaBuilder.and(predicate1, predicate2) : criteriaBuilder.or(predicate1, predicate3);
        }
    }

    private static Predicate getPredicate(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, Predicate predicate, Class<?> keyType, FilterType filterType, List<?> values, List<?> nonNullValues) {
        switch (filterType) {
            case notContains -> notContains(filterCriteria, root, criteriaBuilder, predicate, filterType, values);
            case gte ->
                    greaterThen(filterCriteria, root, criteriaBuilder, predicate, filterType, nonNullValues, keyType);
            case lte -> lessThen(filterCriteria, root, criteriaBuilder, predicate, filterType, nonNullValues, keyType);
            default -> {
            }
        }
        return predicate;
    }

    private static final BiFunction<FilterCriteria, Root<?>, Class<?>> BI_FUNCTION = (filterCriteria, root) -> {
        if (filterCriteria.getKey().contains(".")) {
            final List<String> filterKeys = SPLIT_FUNCTION.apply(filterCriteria.getKey());
            return root.join(filterKeys.get(0)).get(filterKeys.get(1)).getJavaType();
        } else {
            return root.get(filterCriteria.getKey()).getJavaType();
        }
    };

    @Nullable
    private static Predicate getPredicateWithEqOrNq(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, Predicate predicate, FilterType filterType, List<?> values, List<?> nonNullValues, Class<?> keyType) {
        if (filterType.name().equalsIgnoreCase("eq")) {
            return eq(filterCriteria, root, criteriaBuilder, predicate, filterType, values, nonNullValues);
        } else if (filterType.name().equalsIgnoreCase("eqIgnoreCase")) {
            return eqIgnoreCase(filterCriteria, root, criteriaBuilder, predicate, filterType, values, nonNullValues);
        } else if (filterType.name().equalsIgnoreCase("nq")) {
            return notEq(filterCriteria, root, criteriaBuilder, predicate, filterType, values, nonNullValues);
        } else if (filterType.name().equalsIgnoreCase("contains")) {
            return contains(filterCriteria, root, criteriaBuilder, predicate, filterType, values);
        } else {
            return getPredicate(filterCriteria, root, criteriaBuilder, predicate, keyType, filterType, values, nonNullValues);
        }
    }

    private static Predicate contains(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, Predicate predicate, FilterType filterType, List<?> values) {
        if (filterType.name().equalsIgnoreCase(FilterType.contains.toString())) {
            if (filterCriteria.getKey().contains(".")) {
                final List<String> filterKeys = SPLIT_FUNCTION.apply(filterCriteria.getKey());
                predicate = criteriaBuilder.like(root.join(filterKeys.get(0)).get(filterKeys.get(1)).as(String.class), "%" + values.get(0) + "%");
            } else {
                predicate = criteriaBuilder.like(root.get(filterCriteria.getKey()).as(String.class), "%" + values.get(0) + "%");
            }
        }
        return predicate;
    }

    private static Predicate notContains(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, Predicate predicate, FilterType filterType, List<?> values) {
        if (filterType.name().equalsIgnoreCase(FilterType.notContains.toString())) {
            if (filterCriteria.getKey().contains(".")) {
                final List<String> filterKeys = SPLIT_FUNCTION.apply(filterCriteria.getKey());
                predicate = criteriaBuilder.not(criteriaBuilder.like(root.join(filterKeys.get(0)).get(filterKeys.get(1)).as(String.class), "%" + values.get(0) + "%"));
            } else {
                predicate = criteriaBuilder.not(criteriaBuilder.like(root.get(filterCriteria.getKey()).as(String.class), "%" + values.get(0) + "%"));
            }
        }
        return predicate;
    }

    private static Predicate eq(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, Predicate predicate, FilterType filterType, List<?> values, List<?> nonNullValues) {
        if (filterType.name().equalsIgnoreCase(FilterType.eq.toString())) {
            predicate = getPredicateKey(filterCriteria, root, criteriaBuilder, values, nonNullValues, false);
        }
        return predicate;
    }

    private static Predicate notEq(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, Predicate predicate, FilterType filterType, List<?> values, List<?> nonNullValues) {
        if (filterType.name().equalsIgnoreCase(FilterType.nq.toString())) {
            predicate = getPredicate(filterCriteria, root, criteriaBuilder, values, nonNullValues);
        }
        return predicate;
    }

    private static Predicate eqIgnoreCase(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, Predicate predicate, FilterType filterType, List<?> values, List<?> nonNullValues) {
        if (filterType.name().equalsIgnoreCase(FilterType.eqIgnoreCase.toString())) {
            predicate = getPredicateKey(filterCriteria, root, criteriaBuilder, values, nonNullValues, true);
        }
        return predicate;
    }

    private static Predicate greaterThen(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, Predicate predicate, FilterType filterType, List<?> nonNullValues, Class<?> keyType) {
        if (Objects.nonNull(nonNullValues) && nonNullValues.size() == 1) {
            if (filterType.name().equalsIgnoreCase(FilterType.gte.toString())) {
                if (filterCriteria.getKey().contains(".")) {
                    final List<String> filterKeys = SPLIT_FUNCTION.apply(filterCriteria.getKey());
                    predicate = criteriaBuilder.greaterThanOrEqualTo(root.join(filterKeys.get(0)).get(filterKeys.get(1)), nonNullValues.get(0).toString());
                } else {
                    predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(filterCriteria.getKey()), nonNullValues.get(0).toString());
                }
            }
        }
        return predicate;
    }

    private static Predicate lessThen(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, Predicate predicate, FilterType filterType, List<?> nonNullValues, Class<?> keyType) {
        if (Objects.nonNull(nonNullValues) && nonNullValues.size() == 1) {
            if (filterType.name().equalsIgnoreCase(FilterType.lte.toString())) {
                if (filterCriteria.getKey().contains(".")) {
                    final List<String> filterKeys = SPLIT_FUNCTION.apply(filterCriteria.getKey());
                    predicate = criteriaBuilder.lessThanOrEqualTo(root.join(filterKeys.get(0)).get(filterKeys.get(1)), nonNullValues.get(0).toString());
                } else {
                    predicate = criteriaBuilder.lessThanOrEqualTo(root.get(filterCriteria.getKey()), nonNullValues.get(0).toString());
                }
            }
        }
        return predicate;
    }

    private static Predicate getPredicateKey(FilterCriteria filterCriteria, Root<?> root, CriteriaBuilder criteriaBuilder, List<?> values, List<?> nonNullValues, Boolean eqIgnoreCase) {
        if (filterCriteria.getKey().contains(".")) {
            final List<String> filterKeys = SPLIT_FUNCTION.apply(filterCriteria.getKey());
            return eqIgnoreCase ? criteriaBuilder.lower(root.join(filterKeys.get(0)).get(filterKeys.get(1)))
                    .in(nonNullValues.stream().map(value -> isValueNumeric(value.toString()) ? value : value.toString().toLowerCase())
                            .collect(Collectors.toList())) : root.join(filterKeys.get(0)).get(filterKeys.get(1)).in(nonNullValues);
        } else {
            Predicate predicate1 = eqIgnoreCase ? criteriaBuilder.lower(root.get(filterCriteria.getKey())).in(nonNullValues.stream().map(value -> isValueNumeric(value.toString()) ? value : value.toString().toLowerCase()).collect(Collectors.toList())) : root.get(filterCriteria.getKey()).in(nonNullValues);
            Predicate predicate2 = root.get(filterCriteria.getKey()).isNull();
            return values.contains(null) ? nonNullValues.isEmpty() ? predicate2 : criteriaBuilder.or(predicate1, predicate2) : predicate1;
        }
    }

    private static boolean isValueNumeric(String value) {
        return Pattern.matches(NUMERIC_REGEX, value);
    }


}
