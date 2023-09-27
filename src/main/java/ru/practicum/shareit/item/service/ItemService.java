package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, int userId);
    ItemDto update(int userId, int itemId, ItemDto itemDto);
    ItemDto getById(int id);
    Collection<ItemDto> getAllUserItems(int userId);
    Collection<ItemDto> search(String query);
}
