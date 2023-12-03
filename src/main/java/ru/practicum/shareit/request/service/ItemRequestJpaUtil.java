package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;

@Component
@RequiredArgsConstructor
public class ItemRequestJpaUtil {
    private final ItemRequestJpaRepository irRepository;

    public void assertExists(int requestId) {
        if (!irRepository.existsById(requestId)) {
            throw new NotFoundException("Item request with id " + requestId + " not found");
        }
    }

    public ItemRequest requireFindById(int requestId) {
        assertExists(requestId);
        return irRepository.getReferenceById(requestId);
    }
}
