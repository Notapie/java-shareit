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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    @MockBean
    private final BookingService bookingServiceMock;

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final int bookerId = 1;
    final int ownerId = 2;

    private final BookingRequestDto requestDto = new BookingRequestDto(1, LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusDays(1));

    private final Booking expectedBooking = Booking.builder()
            .id(1)
            .item(Item.builder()
                    .id(requestDto.getItemId())
                    .name("item")
                    .isAvailable(true)
                    .owner(User.builder()
                            .id(ownerId)
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

    @Test
    @DisplayName("should success map and approve waiting booking")
    public void approveBooking() throws Exception {
        final Booking.Status newStatus = Booking.Status.APPROVED;
        when(bookingServiceMock.approve(expectedBooking.getId(),
                expectedBooking.getItem().getOwner().getId(), true))
                .thenReturn(expectedBooking.toBuilder().status(newStatus).build());

        mvc.perform(patch("/bookings/" + expectedBooking.getId())
                        .header("X-Sharer-User-Id", expectedBooking.getItem().getOwner().getId())
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.name", is(expectedResponse.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(newStatus.toString())))
                .andExpect(jsonPath("$.item.name", is(expectedResponse.getItem().getName())));
    }

    @Test
    @DisplayName("should success map and get booking by id")
    public void getBookingById() throws Exception {
        when(bookingServiceMock.getById(expectedBooking.getId(), bookerId))
                .thenReturn(expectedBooking);

        mvc.perform(get("/bookings/" + expectedBooking.getId())
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.name", is(expectedResponse.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(expectedBooking.getStatus().toString())))
                .andExpect(jsonPath("$.item.name", is(expectedResponse.getItem().getName())));
    }

    @Test
    @DisplayName("should success map and get all booker bookings")
    public void getAllBookerBookings() throws Exception {
        final String state = "ALL";
        final int from = 0;
        final int size = 10;
        when(bookingServiceMock.getAllForBooker(bookerId, state, from, size))
                .thenReturn(List.of(expectedBooking));

        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", bookerId)
                        .param("state", state)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].booker.name", is(expectedResponse.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(expectedBooking.getStatus().toString())))
                .andExpect(jsonPath("$.[0].item.name", is(expectedResponse.getItem().getName())));
    }

    @Test
    @DisplayName("should success map and get all item owner bookings")
    public void getAllItemOwnerBookings() throws Exception {
        final String state = "ALL";
        final int from = 0;
        final int size = 10;
        when(bookingServiceMock.getAllForOwner(ownerId, state, from, size))
                .thenReturn(List.of(expectedBooking));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", state)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].booker.name", is(expectedResponse.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(expectedBooking.getStatus().toString())))
                .andExpect(jsonPath("$.[0].item.name", is(expectedResponse.getItem().getName())));
    }
}
