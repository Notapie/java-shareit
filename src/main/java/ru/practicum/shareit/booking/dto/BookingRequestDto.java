package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class BookingRequestDto {
    Integer item_id;

    LocalDateTime start;
    LocalDateTime end;
}
