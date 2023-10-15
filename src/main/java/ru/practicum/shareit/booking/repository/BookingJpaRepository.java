package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {
    @Query("from Booking where endTime >= ?1 and startTime <= ?2 and status = 'APPROVED'")
    List<Booking> findBookingsBetweenDates(LocalDateTime firstDate, LocalDateTime secondDate);

    List<Booking> findBookingsByBooker_Id(int bookerId);

    List<Booking> findBookingsByBooker_IdAndStatus(int bookerId, Booking.Status status);

    List<Booking> findBookingsByBooker_IdAndStatusAndEndTimeIsBefore(int bookerId, Booking.Status status,
                                                                     LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndStatusAndStartTimeIsAfter(int bookerId, Booking.Status status,
                                                                      LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndStatusAndStartTimeIsBeforeAndEndTimeIsAfter(int bookerId,
                                                                                        Booking.Status status,
                                                                                        LocalDateTime firstDate,
                                                                                        LocalDateTime secondDate);

}
