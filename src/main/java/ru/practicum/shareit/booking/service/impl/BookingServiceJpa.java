package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingObjectMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.SaveException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
@Primary
@RequiredArgsConstructor
public class BookingServiceJpa implements BookingService {
    private final BookingJpaRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking create(BookingRequestDto bookingRequestDto, int bookerId) {
        // validate request
        validateToCreate(bookingRequestDto);

        // get booker
        final User booker = userService.getById(bookerId);

        // get item
        final Item item = itemService.getById(bookingRequestDto.getItemId());

        // check if item available
        if (!item.getIsAvailable()) {
            throw new ForbiddenException("Cannot to book an unavailable item");
        }

        // check if there is an available time for booking
        if (!isTimeRangeAvailableToBook(bookingRequestDto.getStart(), bookingRequestDto.getEnd())) {
            throw new ForbiddenException("It is not possible to book an item for this time range");
        }

        // create booking entity
        final Booking booking = BookingObjectMapper.fromBookingRequestDto(bookingRequestDto, item, booker);

        try {
            // save new booking
            final Booking savedBooking = bookingRepository.save(booking);
            log.debug("Booking created. " + savedBooking);

            return savedBooking;
        } catch (Exception e) {
            throw new SaveException("Failed to create a new booking. " + booking, e);
        }
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

    private void validateToCreate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getItemId() == null) {
            throw new ValidationException("Item id cannot be null");
        }

        if (bookingRequestDto.getStart() == null) {
            throw new ValidationException("The booking start time cannot be null");
        }

        if (bookingRequestDto.getEnd() == null) {
            throw new ValidationException("The booking end time cannot be null");
        }

        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("The booking start time cannot be in the past");
        }

        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())
                || bookingRequestDto.getEnd().isEqual(bookingRequestDto.getStart())) {
            throw new ValidationException("The booking start time cannot be later or equal to the end time");
        }
    }

    private Collection<Booking> getBookingsBetween(LocalDateTime firstDate, LocalDateTime secondDate) {
        return bookingRepository.findBookingsBetweenDates(firstDate, secondDate);
    }

    private boolean isTimeRangeAvailableToBook(LocalDateTime startTime, LocalDateTime endTime) {
        return getBookingsBetween(startTime, endTime).isEmpty();
    }
}
