package com.amsidh.mvc.repository.core;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
@EqualsAndHashCode
public class OffsetLimitPageable implements Pageable {
    private final int limit;
    private final int offset;

    @Override
    public int getPageNumber() {
        return 1;
    }

    @Override
    public int getPageSize() {
        return this.limit;
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

    @Override
    public Sort getSort() {
        return Sort.unsorted();
    }

    @Override
    public Pageable next() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public Pageable previousOrFirst() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public Pageable first() {
        throw new IllegalArgumentException("Not implemented");
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return Pageable.ofSize(limit);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
