package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingService {
    Booking create(BookingRequestDto bookingRequestDto, int bookerId);

    Booking changeStatus(String newStatus, int ownerId);

    Booking getById(int bookingId, int userId);

    Collection<Booking> getAllForBooker(int bookerId, String bookingState);

    Collection<Booking> getAllForOwner(int ownerId, String bookingState);
}
