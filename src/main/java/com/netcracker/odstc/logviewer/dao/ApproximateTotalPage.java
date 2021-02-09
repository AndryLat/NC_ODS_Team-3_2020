package com.netcracker.odstc.logviewer.dao;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ApproximateTotalPage<T> extends PageImpl<T> {

    private final boolean approximate;

    public ApproximateTotalPage(List<T> content, Pageable pageable, long total, boolean approximate) {
        super(content, pageable, total);
        this.approximate = approximate;
    }

    public ApproximateTotalPage(List<T> content, boolean approximate) {
        super(content);
        this.approximate = approximate;
    }

    public boolean isApproximate() {
        return approximate;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
