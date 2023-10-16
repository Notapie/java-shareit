package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.NotFoundException;

@Component
@RequiredArgsConstructor
public class BookingJpaUtil {
    private final BookingJpaRepository bookingRepository;

    public void assertExists(int bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Booking with id " + bookingId + " not found");
        }
    }

    public Booking requireFindById(int bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Booking with id " + bookingId + " not found");
        }
        return bookingRepository.getReferenceById(bookingId);
    }
}
