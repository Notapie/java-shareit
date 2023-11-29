package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
public class ItemRequestDto {
    String name;
    String description;
    Boolean available;
    Integer requestId;
}
