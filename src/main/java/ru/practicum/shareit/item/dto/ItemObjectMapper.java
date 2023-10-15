package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemObjectMapper {
    public static Item fromItemRequestDto(ItemRequestDto itemRequestDto) {
        return Item.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .isAvailable(itemRequestDto.getAvailable())
                .build();
    }

    public static Item fromItemRequestDto(ItemRequestDto itemRequestDto, User owner) {
        return Item.builder()
                .owner(owner)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .isAvailable(itemRequestDto.getAvailable())
                .build();
    }

    public static ItemResponseDto toItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public static Collection<ItemResponseDto> toItemResponseDto(Collection<Item> items) {
        return items.stream()
                .map(ItemObjectMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }
}
