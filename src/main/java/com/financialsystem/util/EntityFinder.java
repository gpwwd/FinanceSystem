package com.financialsystem.util;

import com.financialsystem.repository.GenericRepository;
import org.springframework.stereotype.Component;

@Component
public class EntityFinder {
    public <T> T findEntityById(Long id, GenericRepository<T> repository, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(entityName + " с id = " + id + " не найден"));
    }
}
