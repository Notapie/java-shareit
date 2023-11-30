package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingObjectMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    @MockBean
    BookingService bookingServiceMock;

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final BookingRequestDto requestDto = new BookingRequestDto(1, LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1));

    private final int bookerId = 1;

    private final Booking expectedBooking = Booking.builder()
            .id(1)
            .item(Item.builder()
                    .id(requestDto.getItemId())
                    .name("item")
                    .isAvailable(true)
                    .owner(User.builder()
                            .id(2)
                            .name("owner name")
                            .email("owner email")
                            .build()
                    )
                    .build()
            )
            .booker(User.builder()
                    .id(bookerId)
                    .name("booker name")
                    .email("booker email")
                    .build()
            )
            .startTime(requestDto.getStart())
            .endTime(requestDto.getEnd())
            .status(Booking.Status.WAITING)
            .build();

    private final BookingResponseDto expectedResponse = BookingObjectMapper.toBookingResponseDto(expectedBooking);

    @Test
    @DisplayName("should success map and create new booking")
    public void createNewBooking() throws Exception {
        when(bookingServiceMock.create(requestDto, bookerId))
                .thenReturn(expectedBooking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.name", is(expectedResponse.getBooker().getName())))
                .andExpect(jsonPath("$.item.name", is(expectedResponse.getItem().getName())));
    }
}
