package com.amsidh.mvc.graphql.filters;

import java.util.Objects;

public class SortBy {
  private String key;

  private SortOrder order = SortOrder.ASC;

  public SortBy() {
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public SortOrder getOrder() {
    return order;
  }

  public void setOrder(SortOrder order) {
    this.order = order;
  }

  @Override
  public String toString() {
    return "SortBy{key='" + key + "', order='" + order + "'}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SortBy that = (SortBy) o;
    return Objects.equals(key, that.key) &&
        Objects.equals(order, that.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, order);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private String key;

    private SortOrder order = SortOrder.ASC;

    public SortBy build() {
      SortBy result = new SortBy();
      result.key = this.key;
      result.order = this.order;
      return result;
    }

    public Builder key(String key) {
      this.key = key;
      return this;
    }

    public Builder order(SortOrder order) {
      this.order = order;
      return this;
    }
  }
}
