package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemObjectMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class IRObjectMapper {
    public static ItemRequest fromRequestDto(IRRequestDto irRequestDto, User owner) {
        return ItemRequest.builder()
                .description(irRequestDto.getDescription())
                .owner(owner)
                .build();
    }

    public static IRResponseDto toResponseDto(ItemRequest itemRequest) {
        return IRResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(ItemObjectMapper.toItemResponseDto(itemRequest.getItems()))
                .build();
    }

    public static Collection<IRResponseDto> toResponseDto(Collection<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(IRObjectMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
