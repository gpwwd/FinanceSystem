package com.financialsystem.util;

import com.financialsystem.repository.GenericRepository;

public class EntityFinder {
    public static <T> T findEntityById(Long id, GenericRepository<T> repository, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(entityName + " с id = " + id + " не найден"));
    }
}
