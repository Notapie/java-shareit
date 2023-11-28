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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceJpaTest {
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

    @Test
    @DisplayName("should success create new booking")
    public void shouldSuccessCreateNewBooking() {
        final User itemOwner = new User(1, "itemOwner@yandex.ru", "Item Owner");
        final User itemBooker = new User(2, "itemBooker@yandex.ru", "Item Booker");
        final Item bookedItem = new Item(
                1,
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
        final Booking expectedBookingToSave = BookingObjectMapper.fromBookingRequestDto(bookingRequestDto, bookedItem,
                itemBooker);
        expectedBookingToSave.setStatus(Booking.Status.WAITING);
        final Booking expectedSavedBooking = expectedBookingToSave.toBuilder().id(1).build();

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
}
