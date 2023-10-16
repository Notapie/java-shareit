package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {
    @Query("from Booking where item.id = ?1 and startTime <= ?3 and endTime >= ?2 and status = 'APPROVED'")
    List<Booking> findBookingsBetweenDates(int itemId, LocalDateTime firstDate, LocalDateTime secondDate);

    // booker searching
    List<Booking> findBookingsByBooker_IdOrderByStartTimeDesc(int bookerId);

    List<Booking> findBookingsByBooker_IdAndStatusOrderByStartTimeDesc(int bookerId, Booking.Status status);

    List<Booking> findBookingsByBooker_IdAndEndTimeIsBeforeOrderByStartTimeDesc(int bookerId, LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndStartTimeIsAfterOrderByStartTimeDesc(int bookerId, LocalDateTime time);

    List<Booking> findBookingsByBooker_IdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(int bookerId, LocalDateTime firstDate, LocalDateTime secondDate);

    // owner searching
    List<Booking> findBookingsByItem_Owner_idOrderByStartTimeDesc(int ownerId);

    List<Booking> findBookingsByItem_Owner_idAndStatusOrderByStartTimeDesc(int ownerId, Booking.Status status);

    List<Booking> findBookingsByItem_Owner_idAndEndTimeIsBeforeOrderByStartTimeDesc(int ownerId, LocalDateTime time);

    List<Booking> findBookingsByItem_Owner_idAndStartTimeIsAfterOrderByStartTimeDesc(int ownerId, LocalDateTime time);

    List<Booking> findBookingsByItem_Owner_idAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(int ownerId, LocalDateTime firstDate, LocalDateTime secondDate);

    // item last bookings
    @Query("from Booking where item.id in ?1 and status = 'APPROVED' and startTime <= ?2 group by item.id order by startTime desc")
    List<Booking> findLastBookingsByItemIds(Collection<Integer> itemIds, LocalDateTime currentTime);

    @Query("from Booking where item.id = ?1 and status = 'APPROVED' and startTime <= ?2 order by startTime desc")
    List<Booking> findLastBookingByItemId(int itemId, LocalDateTime currentTime, Pageable pageable);

    // item next bookings
    @Query("from Booking where item.id in ?1 and status = 'APPROVED' and startTime > ?2 group by item.id order by startTime asc")
    List<Booking> findNextBookingsByItemIds(Collection<Integer> itemIds, LocalDateTime currentTime);

    @Query("from Booking where item.id in ?1 and status = 'APPROVED' and startTime > ?2 order by startTime asc")
    List<Booking> findNextBookingByItemId(int itemId, LocalDateTime currentTime, Pageable pageable);
}
