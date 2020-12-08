package com.netcracker.odstc.logviewer.dao;

import java.math.BigInteger;
import java.util.List;

public interface DAO<T> {
    void save(T type);
    void update(T type);
    void deleteById(BigInteger id);
    T get(BigInteger id);
    List<T> getAll();
}
