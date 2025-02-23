package com.financialsystem.util;

import com.financialsystem.repository.GenericRepository;
import org.springframework.stereotype.Component;

@Component
public class EntityFinder {
    public <T, E> T findEntityById(Long id, GenericRepository<T, E> repository, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException(entityName + " с id = " + id + " не найден"));
    }
}
