package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

@Value
@Builder
public class BookingResponseDto {
    Integer id;

    ItemResponseDto item;
    UserResponseDto booker;

    LocalDateTime start;
    LocalDateTime end;

    Booking.Status status;
}
