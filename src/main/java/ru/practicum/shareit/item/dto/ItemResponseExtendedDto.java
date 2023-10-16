package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@Builder(toBuilder = true)
public class ItemResponseExtendedDto {
    Integer id;
    String name;
    String description;
    Boolean available;
    BookingShortResponseDto lastBooking;
    BookingShortResponseDto nextBooking;
}
