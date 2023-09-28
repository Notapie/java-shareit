package ru.practicum.shareit.item.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
    public ItemResponseDto create(ItemRequestDto itemRequestDto, int userId) {
        return null;
    }

    @Override
    public ItemResponseDto update(int userId, int itemId, ItemRequestDto itemRequestDto) {
        return null;
    }

    @Override
    public ItemResponseDto getById(int id) {
        return null;
    }

    @Override
    public Collection<ItemResponseDto> getAllUserItems(int userId) {
        return null;
    }

    @Override
    public Collection<ItemResponseDto> search(String query) {
        return null;
    }
}
