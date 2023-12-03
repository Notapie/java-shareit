package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceJpa;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceJpa;

import java.time.LocalDateTime;
import java.util.Collection;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceIntegrationTest {
    private final BookingServiceJpa bookingService;
    private final UserServiceJpa userService;
    private final ItemServiceJpa itemService;

    @Test
    @DisplayName("should success create new booking")
    public void shouldSuccessCreateNewBooking() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));
        final Item bookedItem = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        final Booking savedBooking = bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)), booker.getId());

        Assertions.assertEquals(booker.getId(), savedBooking.getBooker().getId());
    }

    @Test
    @DisplayName("should approve booking")
    public void approveBooking() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));
        final Item bookedItem = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        final Booking savedBooking = bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)), booker.getId());

        final Booking approvedBooking = bookingService.approve(savedBooking.getId(), owner.getId(), true);

        Assertions.assertEquals(savedBooking.getId(), approvedBooking.getId());
        Assertions.assertEquals(Booking.Status.APPROVED, approvedBooking.getStatus());
    }

    @Test
    @DisplayName("should get all booker bookings")
    public void getAllBookerBookings() throws InterruptedException {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));
        final Item bookedItem = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        final Booking futureBooking = bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)), booker.getId());

        final Booking currentBooking = bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusMinutes(1)), booker.getId());

        final Booking pastBooking = bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)), booker.getId());

        Thread.sleep(3500);

        final Booking resultCurrent = bookingService.getAllForBooker(booker.getId(), "CURRENT",  0, 10).iterator().next();
        final Booking resultPast = bookingService.getAllForBooker(booker.getId(), "PAST",  0, 10).iterator().next();
        final Booking resultFuture = bookingService.getAllForBooker(booker.getId(), "FUTURE",  0, 10).iterator().next();
        final Collection<Booking> resultWaiting = bookingService.getAllForBooker(booker.getId(), "WAITING",  0, 10);

        Assertions.assertEquals(futureBooking.getId(), resultFuture.getId());
        Assertions.assertEquals(currentBooking.getId(), resultCurrent.getId());
        Assertions.assertEquals(pastBooking.getId(), resultPast.getId());
        Assertions.assertEquals(3, resultWaiting.size());
    }

    @Test
    @DisplayName("should get all owner bookings")
    public void getAllOwnerBookings() throws InterruptedException {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));
        final Item bookedItem = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        final Booking futureBooking = bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)), booker.getId());

        final Booking currentBooking = bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusMinutes(1)), booker.getId());

        final Booking pastBooking = bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)), booker.getId());

        Thread.sleep(3500);

        final Booking resultCurrent = bookingService.getAllForOwner(owner.getId(), "CURRENT",  0, 10).iterator().next();
        final Booking resultPast = bookingService.getAllForOwner(owner.getId(), "PAST",  0, 10).iterator().next();
        final Booking resultFuture = bookingService.getAllForOwner(owner.getId(), "FUTURE",  0, 10).iterator().next();
        final Collection<Booking> resultWaiting = bookingService.getAllForOwner(owner.getId(), "WAITING",  0, 10);

        Assertions.assertEquals(futureBooking.getId(), resultFuture.getId());
        Assertions.assertEquals(currentBooking.getId(), resultCurrent.getId());
        Assertions.assertEquals(pastBooking.getId(), resultPast.getId());
        Assertions.assertEquals(3, resultWaiting.size());
    }

    // exceptions

    @Test
    @DisplayName("cannot create booking if item is not available")
    public void cannotCreateIfUnavailable() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));
        final Item bookedItem = itemService.create(new ItemRequestDto("test item", "test item desc",
                false, null), owner.getId());

        Assertions.assertThrows(UnavailableException.class, () -> {
            bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusDays(1)), booker.getId());
        });
    }

    @Test
    @DisplayName("should error if get with unknown state")
    public void unknownState() {
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));

        Assertions.assertThrows(UnknownStateException.class, () -> {
            bookingService.getAllForBooker(booker.getId(), "qwerty", 0, 10);
        });
        Assertions.assertThrows(UnknownStateException.class, () -> {
            bookingService.getAllForOwner(booker.getId(), "qwerty", 0, 10);
        });
    }

    @Test
    @DisplayName("should error if invalid pageable params")
    public void invalidPageable() {
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));

        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.getAllForBooker(booker.getId(), "qwerty", 0, 0);
        });
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.getAllForOwner(booker.getId(), "qwerty", -1, 10);
        });
    }

    @Test
    @DisplayName("should error if invalid create")
    public void invalidCreate() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));
        final Item bookedItem = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        // null item id
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.create(new BookingRequestDto(null, LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusDays(1)), booker.getId());
        });

        // null start time
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.create(new BookingRequestDto(bookedItem.getId(), null,
                    LocalDateTime.now().plusDays(1)), booker.getId());
        });

        // null end time
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusHours(1),
                    null), booker.getId());
        });

        // start is before now
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().minusHours(1),
                    LocalDateTime.now().plusDays(1)), booker.getId());
        });

        // start is before end
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusMinutes(1)), booker.getId());
        });
    }

    @Test
    @DisplayName("should error if user not owner or booker")
    public void errorIfNotFound() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));
        final Item bookedItem = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        final Booking savedBooking = bookingService.create(new BookingRequestDto(bookedItem.getId(), LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)), booker.getId());

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getById(savedBooking.getId(), 145);
        });
    }
}
