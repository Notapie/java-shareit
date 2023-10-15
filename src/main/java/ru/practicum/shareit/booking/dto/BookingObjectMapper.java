package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemObjectMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserObjectMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class BookingObjectMapper {
    public static Booking fromBookingRequestDto(BookingRequestDto bookingRequestDto, Item item, User booker) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .startTime(bookingRequestDto.getStart())
                .endTime(bookingRequestDto.getEnd())
                .build();
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .item(ItemObjectMapper.toItemResponseDto(booking.getItem()))
                .booker(UserObjectMapper.toUserResponseDto(booking.getBooker()))
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .status(booking.getStatus())
                .build();
    }

    public static Collection<BookingResponseDto> toBookingResponseDto(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingObjectMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}
