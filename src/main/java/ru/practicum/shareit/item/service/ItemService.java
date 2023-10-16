package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseExtendedDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item create(ItemRequestDto itemRequestDto, int userId);

    Item update(int userId, int itemId, ItemRequestDto itemRequestDto);

    ItemResponseExtendedDto getById(int itemId, int userId);

    Collection<ItemResponseExtendedDto> getAllUserItems(int userId);

    Collection<Item> search(String query);
}
