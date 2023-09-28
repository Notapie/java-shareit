package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;

public interface ItemService {
    ItemResponseDto create(ItemRequestDto itemRequestDto, int userId);
    ItemResponseDto update(int userId, int itemId, ItemRequestDto itemRequestDto);
    ItemResponseDto getById(int id);
    Collection<ItemResponseDto> getAllUserItems(int userId);
    Collection<ItemResponseDto> search(String query);
}
