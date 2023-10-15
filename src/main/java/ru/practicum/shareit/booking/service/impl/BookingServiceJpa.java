package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@Service
@Slf4j
@Primary
@RequiredArgsConstructor
public class BookingServiceJpa implements BookingService {
    private final BookingJpaRepository bookingRepository;

    @Override
    public Booking create(BookingRequestDto bookingRequestDto, int bookerId) {
        return null;
    }

    @Override
    public Booking changeStatus(int bookingId, int ownerId, String newStatus) {
        return null;
    }

    @Override
    public Booking getById(int bookingId, int userId) {
        return null;
    }

    @Override
    public Collection<Booking> getAllForBooker(int bookerId, String bookingState) {
        return null;
    }

    @Override
    public Collection<Booking> getAllForOwner(int ownerId, String bookingState) {
        return null;
    }
}
