package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {
    @Query("from Booking where endTime >= ?1 and startTime <= ?2 and status = Booking.Status.APPROVED")
    List<Booking> findBookingsBetweenDates(LocalDateTime startTime, LocalDateTime endTime);
}
