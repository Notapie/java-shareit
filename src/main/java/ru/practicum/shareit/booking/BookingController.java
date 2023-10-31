package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingObjectMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createNew(@RequestBody BookingRequestDto bookingRequestDto,
                                        @RequestHeader("X-Sharer-User-Id") int bookerId) {
        return BookingObjectMapper.toBookingResponseDto(bookingService.create(bookingRequestDto, bookerId));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable int bookingId,
                                             @RequestHeader("X-Sharer-User-Id") int ownerId,
                                             @RequestParam("approved") boolean isApproved) {
        return BookingObjectMapper.toBookingResponseDto(bookingService.approve(bookingId, ownerId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable int bookingId,
                                             @RequestHeader("X-Sharer-User-Id") int userId) {
        return BookingObjectMapper.toBookingResponseDto(bookingService.getById(bookingId, userId));
    }

    @GetMapping
    public Collection<BookingResponseDto> getAllBookerBookings(@RequestParam(value = "state", required = false)
                                                                   String state,
                                                               @RequestHeader("X-Sharer-User-Id") int bookerId) {
        return BookingObjectMapper.toBookingResponseDto(bookingService.getAllForBooker(bookerId, state));
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getAllOwnerBookings(@RequestParam(value = "state", required = false)
                                                                  String state,
                                                               @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return BookingObjectMapper.toBookingResponseDto(bookingService.getAllForOwner(ownerId, state));
    }
}
