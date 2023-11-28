package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemJpaUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserJpaUtil;

import java.time.LocalDateTime;

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

    BookingServiceJpa bookingService;

    @BeforeEach
    public void getNewBookingService() {
        this.bookingService = new BookingServiceJpa(
                this.bookingRepositoryMock,
                this.bookingUtilMock,
                this.userUtilMock,
                this.itemUtilMock
        );
    }

    @Test
    public void shouldSuccessCreateNewBooking() {

        Mockito
                .when(itemUtilMock.requireFindById(Mockito.anyInt()))
                .thenReturn(new Item(1, "kek", "kek lol", true, new User(1, "asd", "asd"), null));

        bookingService.create(new BookingRequestDto(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1)), 2);

    }



}
