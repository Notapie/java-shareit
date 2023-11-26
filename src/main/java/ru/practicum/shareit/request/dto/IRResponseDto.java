package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Value
@Builder
public class IRResponseDto {
    Integer id;
    String description;
    LocalDateTime created;
    Collection<ItemResponseDto> items;
}
