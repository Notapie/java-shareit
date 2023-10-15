package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {
    @Query("from Booking where item.id = ?1 and endTime >= ?2 and startTime <= ?3 and status = 'APPROVED'")
    List<Booking> findBookingsBetweenDates(int itemId, LocalDateTime firstDate, LocalDateTime secondDate);

    // booker searching
    List<Booking> findBookingsByBooker_IdOrderByStartTimeDesc(int bookerId);

    List<Booking> findBookingsByBooker_IdAndStatusOrderByStartTimeDesc(int bookerId, Booking.Status status);

    List<Booking> findBookingsByBooker_IdAndEndTimeIsBeforeOrderByStartTimeDesc(int bookerId, LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndStartTimeIsAfterOrderByStartTimeDesc(int bookerId, LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(int bookerId, LocalDateTime firstDate, LocalDateTime secondDate);

    // owner searching
    List<Booking> findBookingsByItem_Owner_idOrderByStartTimeDesc(int bookerId);

    List<Booking> findBookingsByItem_Owner_idAndStatusOrderByStartTimeDesc(int bookerId, Booking.Status status);

    List<Booking> findBookingsByItem_Owner_idAndEndTimeIsBeforeOrderByStartTimeDesc(int bookerId, LocalDateTime time);

    List<Booking> findBookingsByItem_Owner_idAndStartTimeIsAfterOrderByStartTimeDesc(int bookerId, LocalDateTime time);

    List<Booking> findBookingsByItem_Owner_idAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(int bookerId, LocalDateTime firstDate, LocalDateTime secondDate);
}
