package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.request.dto.IRResponseDto;

@Value
@Builder
public class ItemResponseDto {
    Integer id;
    String name;
    String description;
    Boolean available;
    Integer requestId;
}
