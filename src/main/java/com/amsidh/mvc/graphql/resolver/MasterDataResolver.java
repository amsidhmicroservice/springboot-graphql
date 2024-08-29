package com.amsidh.mvc.graphql.resolver;

import com.amsidh.mvc.exception.GenericGraphQlException;
import com.amsidh.mvc.exception.TypeConversionException;
import com.amsidh.mvc.graphql.filters.FilterCriteria;
import com.amsidh.mvc.graphql.filters.FilterType;
import com.amsidh.mvc.graphql.filters.SortBy;
import com.amsidh.mvc.repository.core.MasterDataRepository;
import com.amsidh.mvc.repository.core.OffsetLimitPageable;
import com.amsidh.mvc.repository.core.QuerySpecification;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface MasterDataResolver {

    default <T> List<T> getData(MasterDataRepository<T, Long> masterDataRepository, List<FilterCriteria> filterCriteriaList, int offset, int limit, String resolveFor, Class<T> clazz, SortBy sortBy) {
        handleGenericGraphQlException(limit, offset, resolveFor, clazz.getCanonicalName(), filterCriteriaList);
        return getMasterData(masterDataRepository, filterCriteriaList, offset, limit, resolveFor, clazz, sortBy, true);
    }

    default <T> List<T> getMasterData(MasterDataRepository<T, Long> masterDataRepository, List<FilterCriteria> filterCriteriaList, int offset, int limit, String resolveFor, Class<T> clazz, SortBy sortBy, boolean isDistinctQuery) {
        try {
            final List<T> data = offset > -1 ? masterDataRepository.findAll(QuerySpecification.getSpecification(filterCriteriaList, sortBy, isDistinctQuery), new OffsetLimitPageable(limit, offset)).getContent()
                    : masterDataRepository.findAll(QuerySpecification.getSpecification(filterCriteriaList, sortBy, isDistinctQuery));
            if (Objects.nonNull(filterCriteriaList)) {
                checkAndApplyFilter(filterCriteriaList, clazz, data);
            }
            return data;

        } catch (TypeConversionException typeConversionException) {
            throw new GenericGraphQlException("typeConversionMismatch", typeConversionException.getMessage(), true);
        } catch (InvalidDataAccessApiUsageException invalidDataAccessApiUsageException) {
            throw new GenericGraphQlException("invalidDataAccess", invalidDataAccessApiUsageException.getMessage(), true);
        } catch (Exception exception) {
            throw new GenericGraphQlException("internalServerError", exception.getMessage(), true);
        }
    }

    default <T> void checkAndApplyFilter(List<FilterCriteria> filterCriteriaList, Class<T> clazz, List<T> data) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        List<FilterCriteria> list = filterCriteriaList.stream().filter(filterCriteria -> filterCriteria.getKey().contains(".")).collect(Collectors.toList());
        if (!list.isEmpty()) {
            for (FilterCriteria criteria : list) {
                final List<String> keys = Arrays.asList(criteria.getKey().split("\\."));
                final PropertyDescriptor propertyDescriptor = new PropertyDescriptor(keys.get(0), clazz);
                filter(data, criteria, keys, propertyDescriptor);
            }
        }
    }

    default <T> void filter(List<T> data, FilterCriteria criteria, List<String> keys, PropertyDescriptor propertyDescriptor) throws InvocationTargetException, IllegalAccessException, IntrospectionException {
        for (T obj : data) {
            final Object object = propertyDescriptor.getReadMethod().invoke(obj);
            if (object instanceof List) {
                propertyDescriptor.getWriteMethod().invoke(obj, applyFilter(criteria, keys, (List<T>) object));
            }
        }
    }

    default <T> Object applyFilter(FilterCriteria criteria, List<String> keys, List<T> objectList) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        final FilterType filterType = criteria.getFilterInput().getFilterType();
        for (T o : objectList) {
            Boolean flag = true;
            switch (filterType) {
                case eq, eqIgnoreCase ->
                        flag = criteria.getFilterInput().getFilterValues().contains(new PropertyDescriptor(keys.get(1), o.getClass()).getReadMethod().invoke(o).toString());
                case nq ->
                        flag = !criteria.getFilterInput().getFilterValues().contains(new PropertyDescriptor(keys.get(1), o.getClass()).getReadMethod().invoke(o).toString());
                case contains -> flag = getContainsValue(criteria, keys, o);
                case notContains -> flag = getContainsNotValue(criteria, keys, o);
                default -> {
                }
            }
            if (flag) {
                list.add(o);
            }
        }

        return list;
    }

    default <T> Boolean getContainsNotValue(FilterCriteria criteria, List<String> keys, T o) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        boolean flag = false;
        for (String e : criteria.getFilterInput().getFilterValues()) {
            flag = !(new PropertyDescriptor(keys.get(1), o.getClass()).getReadMethod().invoke(o).toString().contains(e));
            if (flag) {
                break;
            }
        }
        return flag;
    }

    default <T> Boolean getContainsValue(FilterCriteria criteria, List<String> keys, T o) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        boolean flag = false;
        for (String e : criteria.getFilterInput().getFilterValues()) {
            flag = (new PropertyDescriptor(keys.get(1), o.getClass()).getReadMethod().invoke(o).toString().contains(e));
            if (flag) {
                break;
            }
        }
        return flag;
    }

    default <T> void handleGenericGraphQlException(int limit, int offset, String resolveFor, String clazz, List<FilterCriteria> filterCriteriaList) {
        if (limit < 1) {
            throw new GenericGraphQlException("invalidRange", "limit should not be less than 1", true);
        }

        if (offset < -1) {
            throw new GenericGraphQlException("invalidRange", "offset should not be less than 0", true);
        }

    }
}
