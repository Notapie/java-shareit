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
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceJpa;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceJpa;

import java.time.LocalDateTime;

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
}
