package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dto.BookingObjectMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
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

        // get item
        final Item item = itemService.getById(bookingRequestDto.getItemId());

        // check if user is booker
        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Booking not found");
        }

        // get booker
        final User booker = userService.getById(bookerId);

        // check if item available
        if (!item.getIsAvailable()) {
            throw new UnavailableException("Cannot to book an unavailable item");
        }

        // check if there is an available time for booking
        if (!isTimeRangeAvailableToBook(item.getId(), bookingRequestDto.getStart(), bookingRequestDto.getEnd())) {
            throw new ForbiddenException("It is not possible to book an item for this time range");
        }

        // create booking entity
        final Booking booking = BookingObjectMapper.fromBookingRequestDto(bookingRequestDto, item, booker);
        booking.setStatus(Booking.Status.WAITING);

        try {
            // save new booking
            final Booking savedBooking = bookingRepository.save(booking);
            log.debug("Booking created. " + savedBooking);

            return savedBooking;
        } catch (Exception e) {
            throw new SaveException("Failed to create a new booking. " + booking, e);
        }
    }

    private Booking changeStatus(int bookingId, int userId, Booking.Status newStatus) {
        // get booking
        Booking booking = getById(bookingId, userId);

        // check that the new status is not equal to the initial one
        if (newStatus == Booking.Status.WAITING) {
            throw new ForbiddenException("It is not possible to change the status to " + Booking.Status.WAITING.name());
        }

        // check if it is possible to change the status
        if (booking.getStatus() != Booking.Status.WAITING) {
            throw new UnavailableException("The " + booking.getStatus().name() + " status cannot be changed");
        }

        // check if there is an available time for booking
        if (!isTimeRangeAvailableToBook(booking.getItem().getId(), booking.getStartTime(), booking.getEndTime())) {
            throw new ForbiddenException("It is not possible to book an item for this time range");
        }

        // only the booker can cancel his booking
        if (newStatus == Booking.Status.CANCELED && booking.getBooker().getId() != userId) {
            throw new NotFoundException("Booking not found");
        }

        // only the item owner can approve or reject booking
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Booking not found");
        }

        // change status and save
        booking.setStatus(newStatus);

        try {
            final Booking savedBooking = bookingRepository.save(booking);
            log.debug("Booking updated. " + savedBooking);

            return savedBooking;
        } catch (Exception e) {
            throw new SaveException("Failed to update a booking status. " + booking, e);
        }
    }

    @Override
    public Booking approve(int bookingId, int ownerId, boolean isApproved) {
        return changeStatus(bookingId, ownerId, isApproved ? Booking.Status.APPROVED : Booking.Status.REJECTED);
    }

    @Override
    public Booking getById(int bookingId, int userId) {
        // get booking
        final Booking booking = requireFindById(bookingId);

        // check if the user is booker or owner
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Booking not found");
        }

        // return the booking
        return booking;
    }

    @Override
    public Collection<Booking> getAllForBooker(int bookerId, String bookingState) {
        userService.getById(bookerId);
        if (!StringUtils.hasText(bookingState) || bookingState.equals("ALL")) {
            return bookingRepository.findBookingsByBooker_IdOrderByStartTimeDesc(bookerId);
        }

        if (bookingState.equals("WAITING") || bookingState.equals("REJECTED")) {
            return bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartTimeDesc(bookerId, Booking.Status.valueOf(bookingState));
        }

        if (bookingState.equals("PAST")) {
            return bookingRepository.findBookingsByBooker_IdAndEndTimeIsBeforeOrderByStartTimeDesc(bookerId,
                    LocalDateTime.now());
        }

        if (bookingState.equals("CURRENT")) {
            return bookingRepository.findBookingsByBooker_IdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(bookerId,
                    LocalDateTime.now(), LocalDateTime.now());
        }

        if (bookingState.equals("FUTURE")) {
            return bookingRepository.findBookingsByBooker_IdAndStartTimeIsAfterOrderByStartTimeDesc(bookerId,
                    LocalDateTime.now());
        }

        throw new UnknownStateException("Invalid state");
    }

    @Override
    public Collection<Booking> getAllForOwner(int ownerId, String bookingState) {
        userService.getById(ownerId);

        if (!StringUtils.hasText(bookingState) || bookingState.equals("ALL")) {
            return bookingRepository.findBookingsByItem_Owner_idOrderByStartTimeDesc(ownerId);
        }

        if (bookingState.equals("WAITING") || bookingState.equals("REJECTED")) {
            return bookingRepository.findBookingsByItem_Owner_idAndStatusOrderByStartTimeDesc(ownerId, Booking.Status.valueOf(bookingState));
        }

        if (bookingState.equals("PAST")) {
            return bookingRepository.findBookingsByItem_Owner_idAndEndTimeIsBeforeOrderByStartTimeDesc(ownerId,
                    LocalDateTime.now());
        }

        if (bookingState.equals("CURRENT")) {
            return bookingRepository.findBookingsByItem_Owner_idAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(ownerId,
                    LocalDateTime.now(), LocalDateTime.now());
        }

        if (bookingState.equals("FUTURE")) {
            return bookingRepository.findBookingsByItem_Owner_idAndStartTimeIsAfterOrderByStartTimeDesc(ownerId,
                    LocalDateTime.now());
        }

        throw new UnknownStateException("Invalid state");
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

    private Collection<Booking> getBookingsBetween(int itemId, LocalDateTime firstDate, LocalDateTime secondDate) {
        return bookingRepository.findBookingsBetweenDates(itemId, firstDate, secondDate);
    }

    private boolean isTimeRangeAvailableToBook(int itemId, LocalDateTime startTime, LocalDateTime endTime) {
        return getBookingsBetween(itemId, startTime, endTime).isEmpty();
    }

    private Booking requireFindById(int bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Booking with id " + bookingId + " not found");
        }
        return bookingRepository.getReferenceById(bookingId);
    }
}
