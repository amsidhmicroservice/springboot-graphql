package com.amsidh.mvc.utils;

import com.amsidh.mvc.graphql.filters.FilterInput;
import com.amsidh.mvc.graphql.filters.FilterType;
import com.amsidh.mvc.exception.TypeConversionException;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@NoArgsConstructor
public final class TypeConvertor {

    public static List<?> mapDataToType(Class<?> keyType, FilterInput filterInput) {
        final List<String> filterValues = filterInput.getFilterValues();
        validateFilterValues(filterInput.getFilterType(), filterValues);
        List<? extends Serializable> filterValues6 = getSerializable1(keyType, filterInput, filterValues);
        if (Objects.nonNull(filterValues6)) {
            return filterValues6;
        }

        List<? extends Serializable> filterValues3 = getSerializable(keyType, filterInput, filterValues);
        if (Objects.nonNull(filterValues3)) {
            return filterValues3;
        }
        List<? extends Serializable> filterValues2 = getSerializable(keyType, filterValues);
        if (Objects.nonNull(filterValues2)) {
            return filterValues2;
        }
        throw new TypeConversionException(String.format("Invalid Filter type %s", keyType.getName()));
    }

    private static List<? extends Serializable> getSerializable(Class<?> keyType, FilterInput filterInput, List<String> filterValues) {
        if (keyType == Double.class || keyType.getName().equalsIgnoreCase("double")) {
            return getList(keyType, filterInput, filterValues);
        }
        return null;
    }

    private static List<? extends Serializable> getSerializable(Class<?> keyType, List<String> filterValues) {
        if (keyType == Long.class || keyType.getName().equalsIgnoreCase("long")) {
            return filterValues.stream().map(str -> {
                try {
                    return Integer.parseInt(str);
                } catch (NumberFormatException numberFormatException) {
                    return handleError(keyType, str);
                }
            }).collect(Collectors.toList());
        }
        return null;
    }

    private static List<? extends Serializable> getSerializable1(Class<?> keyType, FilterInput filterInput, List<String> filterValues) {
        List<? extends Serializable> filterValues6 = getSerializable2(keyType, filterInput, filterValues);
        if (Objects.nonNull(filterValues6)) {
            return filterValues6;
        }
        List<? extends Serializable> filterValues4 = getList(keyType, filterValues);
        if (Objects.nonNull(filterValues4)) {
            return filterValues4;
        }

        List<? extends Serializable> filterValues5 = getSerializable1(keyType, filterValues);
        if (Objects.nonNull(filterValues5)) {
            return filterValues5;
        }
        return null;
    }

    private static List<? extends Serializable> getSerializable1(Class<?> keyType, List<String> filterValues) {
        if (keyType == Float.class || keyType.getName().equalsIgnoreCase("float")) {
            return filterValues.stream().map(str -> {
                try {
                    return Float.parseFloat(str);
                } catch (NumberFormatException numberFormatException) {
                    return handleError(keyType, str);
                }
            }).collect(Collectors.toList());
        }
        return null;
    }

    private static List<? extends Serializable> getList(Class<?> keyType, List<String> filterValues) {

        if (keyType == Integer.class || keyType.getName().equalsIgnoreCase("int")) {
            return filterValues.stream().map(str -> {
                try {
                    return Integer.parseInt(str);
                } catch (NumberFormatException numberFormatException) {
                    return handleError(keyType, str);
                }
            }).collect(Collectors.toList());
        }
        return null;
    }

    private static List<? extends Serializable> getList(Class<?> keyType, FilterInput filterInput, List<String> filterValues) {
        return filterValues.stream().map(str -> {
            try {
                String str1 = getString(keyType, filterInput, str);
                if (Objects.nonNull(str1)) {
                    return str1;
                }
                return Double.parseDouble(str);
            } catch (NumberFormatException numberFormatException) {
                return handleError(keyType, str);
            }
        }).collect(Collectors.toList());
    }

    private static String getString(Class<?> keyType, FilterInput filterInput, String str) {
        if (checkEligibility.test(keyType.getSimpleName(), filterInput.getFilterType().name())) {
            Double.parseDouble(str);
            return str;
        }
        return null;
    }


    private static Boolean handleError(Class<?> keyType, String str) {
        throw new TypeConversionException(String.format("Failed to convert %s to %s", str, keyType.getName()));
    }

    private static List<? extends Serializable> getSerializable2(Class<?> keyType, FilterInput filterInput, List<String> filterValues) {
        List<String> filterValue6 = getStrings(keyType, filterInput, filterValues);
        if (Objects.nonNull(filterValue6)) {
            return filterValue6;
        }
        List<Boolean> filterValue1 = getBooleanValues1(keyType, filterInput, filterValues);
        if (Objects.nonNull(filterValue1)) {
            return filterValue1;
        }
        return null;
    }

    private static List<Boolean> getBooleanValues1(Class<?> keyType, FilterInput filterInput, List<String> filterValues) {
        if (keyType == Boolean.class || keyType.getName().equalsIgnoreCase("boolean")) {
            return filterValues.stream().map(str -> {
                try {
                    checkEligibility.test(keyType.getSimpleName(), filterInput.getFilterType().name());
                    return Boolean.parseBoolean(str);
                } catch (NumberFormatException numberFormatException) {
                    return handleError(keyType, str);
                }
            }).collect(Collectors.toList());
        }
        return null;
    }

    private static List<String> getStrings(Class<?> keyType, FilterInput filterInput, List<String> filterValues) {
        if (keyType == String.class) {
            checkEligibility.test(keyType.getSimpleName(), filterInput.getFilterType().name());
            return filterValues;
        }
        return null;
    }

    private static BiPredicate<String, String> checkEligibility = (keyType, filterName) -> {
        if ((keyType.equalsIgnoreCase("String") || keyType.equalsIgnoreCase("boolean"))
                && (filterName.equalsIgnoreCase("gte") || filterName.equalsIgnoreCase("lte"))) {
            throw new TypeConversionException(String.format("%s filter not applicable for %s", filterName, keyType));
        }
        if ((keyType.equalsIgnoreCase("double")) && (filterName.equalsIgnoreCase("contains") || filterName.equalsIgnoreCase("notContains"))) {
            return true;
        }
        return false;
    };

    private static void validateFilterValues(FilterType filterType, List<String> filterValues) {
        if (filterValues.isEmpty() || hasInvalidLength(filterType, filterValues)) {
            throw new TypeConversionException(String.format("Invalid length %s, of filterValues for filterType %s", filterValues.size(), filterType.name()));
        }
        if (hasInvalidValues(filterType, filterValues)) {
            throw new TypeConversionException(String.format("Invalid values %s, of filterValues for filterType %s", "null", filterType.name()));

        }
    }

    private static boolean hasInvalidValues(FilterType filterType, List<String> filterValues) {
        if (hasInvalidValues1(filterType, filterValues)) {
            return true;
        }
        if (hasInvalidValues2(filterType, filterValues)) {
            return true;
        }

        return false;
    }

    private static boolean hasInvalidValues2(FilterType filterType, List<String> filterValues) {
        if ((filterType.name().equalsIgnoreCase(FilterType.gte.toString())
                || filterType.name().equalsIgnoreCase(FilterType.lte.toString()))
                && filterValues.contains(null)) {
            return true;
        }
        return false;
    }

    private static boolean hasInvalidValues1(FilterType filterType, List<String> filterValues) {
        if ((filterType.name().equalsIgnoreCase(FilterType.notContains.toString())
                || filterType.name().equalsIgnoreCase(FilterType.contains.toString()))
                && filterValues.contains(null)) {
            return true;
        }
        return false;
    }

    private static boolean hasInvalidLength(FilterType filterType, List<String> filterValues) {
        if (hasInvalidLength1(filterType, filterValues)) {
            return true;
        }
        if (hasInvalidLength2(filterType, filterValues)) {
            return true;
        }
        return false;
    }

    private static boolean hasInvalidLength2(FilterType filterType, List<String> filterValues) {
        if ((filterType.name().equalsIgnoreCase(FilterType.gte.toString())
                || filterType.name().equalsIgnoreCase(FilterType.lte.toString()))
                && filterValues.size() > 1) {
            return true;
        }
        return false;
    }

    private static boolean hasInvalidLength1(FilterType filterType, List<String> filterValues) {
        if ((filterType.name().equalsIgnoreCase(FilterType.notContains.toString()) || filterType.name().equalsIgnoreCase(FilterType.contains.toString()))
                && filterValues.size() > 1) {
            return true;
        }
        return false;
    }
}
