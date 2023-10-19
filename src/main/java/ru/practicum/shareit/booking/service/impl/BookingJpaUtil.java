package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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

    public Booking getLastItemBooking(int itemId) {
        List<Booking> result = bookingRepository
                .findLastBookingByItemId(itemId, LocalDateTime.now(), PageRequest.of(0, 1));
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Booking> getLastItemsBookings(Collection<Integer> itemIds) {
        return bookingRepository.findLastBookingsByItemIds(itemIds, LocalDateTime.now());
    }

    public Booking getNextItemBooking(int itemId) {
        List<Booking> result = bookingRepository
                .findNextBookingByItemId(itemId, LocalDateTime.now(), PageRequest.of(0, 1));
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Booking> getNextItemsBookings(Collection<Integer> itemIds) {
        return bookingRepository.findNextBookingsByItemIds(itemIds, LocalDateTime.now());
    }

    public boolean isUserHasBookedItem(int userId, int itemId) {
        return bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndTimeBefore(userId, itemId,
                Booking.Status.APPROVED, LocalDateTime.now());
    }
}
