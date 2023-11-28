package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@RequiredArgsConstructor
public class BookingRequestDto {
    Integer itemId;

    LocalDateTime start;
    LocalDateTime end;
}
