package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingObjectMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemJpaUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserJpaUtil;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceJpaUnitTest {
    @Mock
    BookingJpaRepository bookingRepositoryMock;

    @Mock
    BookingJpaUtil bookingUtilMock;

    @Mock
    UserJpaUtil userUtilMock;

    @Mock
    ItemJpaUtil itemUtilMock;

    @InjectMocks
    BookingServiceJpa bookingService;

    final int itemOwnerId = 1;
    final int itemBookerId = 2;
    final int itemId = 1;
    final int bookingId = 1;

    final User itemOwner = new User(itemOwnerId, "itemOwner@yandex.ru", "Item Owner");
    final User itemBooker = new User(itemBookerId, "itemBooker@yandex.ru", "Item Booker");
    final Item bookedItem = new Item(
            itemId,
            "item name",
            "item description",
            true,
            itemOwner,
            null
    );
    final BookingRequestDto bookingRequestDto = new BookingRequestDto(
            bookedItem.getId(),
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1)
    );
    final Booking expectedSavedBooking = BookingObjectMapper.fromBookingRequestDto(bookingRequestDto, bookedItem,
                    itemBooker).toBuilder()
            .status(Booking.Status.WAITING)
            .id(bookingId).build();

    @Test
    @DisplayName("should success create new booking")
    public void shouldSuccessCreateNewBooking() {
        when(itemUtilMock.requireFindById(bookedItem.getId()))
                .thenReturn(bookedItem);
        when(userUtilMock.requireFindById(itemBooker.getId()))
                .thenReturn(itemBooker);
        when(bookingRepositoryMock.save(any()))
                .thenAnswer(invocationOnMock -> {
                    final Booking bookingParam = invocationOnMock.getArgument(0, Booking.class);
                    return bookingParam.toBuilder().id(1).build();
                });

        final Booking savedBooking = bookingService.create(bookingRequestDto, itemBooker.getId());
        Assertions.assertEquals(expectedSavedBooking.getId(), savedBooking.getId());
        Assertions.assertEquals(expectedSavedBooking.getBooker().getId(), savedBooking.getBooker().getId());
    }

    @Test
    @DisplayName("should success approve booking status")
    public void approveBookingStatus() {
        when(bookingUtilMock.requireFindById(bookingId))
                .thenReturn(expectedSavedBooking);
        final Booking expectedApprovedBooking = expectedSavedBooking.toBuilder()
                .status(Booking.Status.APPROVED).build();
        when(bookingRepositoryMock.save(any()))
                .thenReturn(expectedApprovedBooking);

        final Booking savedBooking = bookingService.approve(
                bookingId,
                itemOwnerId,
                true);

        Assertions.assertEquals(expectedApprovedBooking.getId(), savedBooking.getId());
        Assertions.assertEquals(expectedApprovedBooking.getStatus(), savedBooking.getStatus());
    }

    @Test
    @DisplayName("should success get booking by id")
    public void getBookingById() {
        when(bookingUtilMock.requireFindById(bookingId))
                .thenReturn(expectedSavedBooking);

        final Booking result = bookingService.getById(bookingId, itemOwnerId);

        Assertions.assertEquals(bookingId, result.getId());
    }

    @Test
    @DisplayName("should success get all booker bookings")
    public void getAllBookerBookings() {
        when(bookingRepositoryMock.findBookingsByBookerId(anyInt(), any()))
                .thenReturn(List.of(expectedSavedBooking));

        final Collection<Booking> result = bookingService.getAllForBooker(
                bookingId,
                "ALL",
                0,
                10
        );

        Assertions.assertEquals(bookingId, result.iterator().next().getId());
    }

    @Test
    @DisplayName("should success get all item owner bookings")
    public void getAllOwnerBookings() {
        when(bookingRepositoryMock.findBookingsByItemOwnerId(anyInt(), any()))
                .thenReturn(List.of(expectedSavedBooking));

        final Collection<Booking> result = bookingService.getAllForOwner(
                itemOwnerId,
                "ALL",
                0,
                10
        );

        Assertions.assertEquals(bookingId, result.iterator().next().getId());
    }
}
