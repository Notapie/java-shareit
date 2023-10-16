package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class BookingShortResponseDto {
    Integer id;
    Integer bookerId;
    LocalDateTime start;
    LocalDateTime end;
}
