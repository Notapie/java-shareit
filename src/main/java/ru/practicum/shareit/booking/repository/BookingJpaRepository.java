package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {
    @Query("from Booking where endTime >= ?1 and startTime <= ?2 and status = 'APPROVED'")
    List<Booking> findBookingsBetweenDates(LocalDateTime firstDate, LocalDateTime secondDate);

    // booker searching
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

    // owner searching
    List<Booking> findBookingsByItem_Owner_id(int bookerId);

    List<Booking> findBookingsByItem_Owner_idAndStatus(int bookerId, Booking.Status status);

    List<Booking> findBookingsByItem_Owner_idAndStatusAndEndTimeIsBefore(int bookerId, Booking.Status status,
                                                                     LocalDateTime time);

    List<Booking> findBookingsByItem_Owner_idAndStatusAndStartTimeIsAfter(int bookerId, Booking.Status status,
                                                                      LocalDateTime time);

    List<Booking> findBookingsByItem_Owner_idAndStatusAndStartTimeIsBeforeAndEndTimeIsAfter(int bookerId,
                                                                                        Booking.Status status,
                                                                                        LocalDateTime firstDate,
                                                                                        LocalDateTime secondDate);
}
