package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;

@Component
@RequiredArgsConstructor
public class ItemJpaUtil {
    private final ItemJpaRepository itemRepository;

    public void assertExists(int itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }
    }

    public Item requireFindById(int itemId) {
        assertExists(itemId);
        return itemRepository.getReferenceById(itemId);
    }
}
