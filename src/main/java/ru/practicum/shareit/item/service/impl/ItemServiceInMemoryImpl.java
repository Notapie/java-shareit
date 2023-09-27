package ru.practicum.shareit.item.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceInMemoryImpl implements ItemService {
    private Map<Integer, Item> idToItem;
    private Map<Integer, List<Item>> userIdToItems;

    @Override
    public ItemDto create(ItemDto itemDto, int userId) {
        return null;
    }

    @Override
    public ItemDto update(int userId, int itemId, ItemDto itemDto) {
        return null;
    }

    @Override
    public ItemDto getById(int id) {
        return null;
    }

    @Override
    public Collection<ItemDto> getAllUserItems(int userId) {
        return null;
    }

    @Override
    public Collection<ItemDto> search(String query) {
        return null;
    }
}
