package com.amsidh.mvc.graphql.filters;

import java.util.Objects;

public class FilterCriteria {
  private String key;

  private FilterInput filterInput;

  public FilterCriteria() {
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public FilterInput getFilterInput() {
    return filterInput;
  }

  public void setFilterInput(FilterInput filterInput) {
    this.filterInput = filterInput;
  }

  @Override
  public String toString() {
    return "FilterCriteria{key='" + key + "', filterInput='" + filterInput + "'}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FilterCriteria that = (FilterCriteria) o;
    return Objects.equals(key, that.key) &&
        Objects.equals(filterInput, that.filterInput);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, filterInput);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private String key;

    private FilterInput filterInput;

    public FilterCriteria build() {
      FilterCriteria result = new FilterCriteria();
      result.key = this.key;
      result.filterInput = this.filterInput;
      return result;
    }

    public Builder key(String key) {
      this.key = key;
      return this;
    }

    public Builder filterInput(FilterInput filterInput) {
      this.filterInput = filterInput;
      return this;
    }
  }
}
