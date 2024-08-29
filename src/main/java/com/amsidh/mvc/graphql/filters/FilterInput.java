package com.amsidh.mvc.graphql.filters;

import java.util.List;
import java.util.Objects;

public class FilterInput {
  private FilterType filterType;

  private List<String> filterValues;

  public FilterInput() {
  }

  public FilterType getFilterType() {
    return filterType;
  }

  public void setFilterType(FilterType filterType) {
    this.filterType = filterType;
  }

  public List<String> getFilterValues() {
    return filterValues;
  }

  public void setFilterValues(List<String> filterValues) {
    this.filterValues = filterValues;
  }

  @Override
  public String toString() {
    return "FilterInput{filterType='" + filterType + "', filterValues='" + filterValues + "'}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FilterInput that = (FilterInput) o;
    return Objects.equals(filterType, that.filterType) &&
        Objects.equals(filterValues, that.filterValues);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filterType, filterValues);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private FilterType filterType;

    private List<String> filterValues;

    public FilterInput build() {
      FilterInput result = new FilterInput();
      result.filterType = this.filterType;
      result.filterValues = this.filterValues;
      return result;
    }

    public Builder filterType(FilterType filterType) {
      this.filterType = filterType;
      return this;
    }

    public Builder filterValues(List<String> filterValues) {
      this.filterValues = filterValues;
      return this;
    }
  }
}
