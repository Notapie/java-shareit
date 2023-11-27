package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {
    @Query("from Booking " +
            "where item.id = :itemId " +
            "and startTime <= :secondDate " +
            "and endTime >= :firstDate " +
            "and status = 'APPROVED'")
    List<Booking> findBookingsBetweenDates(int itemId, LocalDateTime firstDate, LocalDateTime secondDate);

    // booker searching
    @Query("from Booking " +
            "where booker.id = :bookerId " +
            "order by startTime desc")
    List<Booking> findBookingsByBookerId(int bookerId, Pageable pageable);

    @Query("from Booking " +
            "where booker.id = :bookerId " +
            "and status = :status " +
            "order by startTime desc")
    List<Booking> findBookingsByBookerIdAndStatus(int bookerId, Booking.Status status, Pageable pageable);

    @Query("from Booking " +
            "where booker.id = :bookerId " +
            "and endTime < :time " +
            "order by startTime desc")
    List<Booking> findBookingsByBookerIdAndEndIsBefore(int bookerId, LocalDateTime time, Pageable pageable);

    @Query("from Booking " +
            "where booker.id = :bookerId " +
            "and startTime > :time " +
            "order by startTime desc")
    List<Booking> findBookingsByBookerIdAndStartIsAfter(int bookerId, LocalDateTime time, Pageable pageable);

    @Query("from Booking " +
            "where booker.id = :bookerId " +
            "and startTime < :currentTime " +
            "and endTime > :currentTime " +
            "order by startTime, endTime desc")
    List<Booking> findCurrentBookingsByBookerIdAndCurrentTime(int bookerId, LocalDateTime currentTime,
                                                              Pageable pageable);

    // owner searching
    @Query("from Booking " +
            "where item.owner.id = :ownerId " +
            "order by startTime desc")
    List<Booking> findBookingsByItemOwnerId(int ownerId, Pageable pageable);

    @Query("from Booking " +
            "where item.owner.id = :ownerId " +
            "and status = :status " +
            "order by startTime desc")
    List<Booking> findBookingsByItemOwnerIdAndStatus(int ownerId, Booking.Status status, Pageable pageable);

    @Query("from Booking " +
            "where item.owner.id = :ownerId " +
            "and endTime < :time " +
            "order by startTime desc")
    List<Booking> findBookingsByItemOwnerIdAndEndIsBefore(int ownerId, LocalDateTime time, Pageable pageable);

    @Query("from Booking " +
            "where item.owner.id = :ownerId " +
            "and startTime > :time " +
            "order by startTime desc")
    List<Booking> findBookingsByItemOwnerIdAndStartIsAfter(int ownerId, LocalDateTime time, Pageable pageable);

    @Query("from Booking " +
            "where item.owner.id = :ownerId " +
            "and startTime < :time " +
            "and endTime > :time " +
            "order by startTime, endTime desc")
    List<Booking> findCurrentBookingsByItemOwnerIdAndCurrentTime(int ownerId, LocalDateTime time, Pageable pageable);

    // item last bookings
    @Query(value = "SELECT DISTINCT ON (item_id) * " +
            "FROM \"booking\" " +
            "WHERE item_id IN :itemIds " +
            "AND status = 'APPROVED' " +
            "AND start_time <= :currentTime " +
            "ORDER BY item_id, start_time DESC", nativeQuery = true)
    List<Booking> findLastBookingsByItemIds(Collection<Integer> itemIds, LocalDateTime currentTime);

    @Query("from Booking " +
            "where item.id = :itemId " +
            "and status = 'APPROVED' " +
            "and startTime <= :currentTime")
    List<Booking> findLastBookingByItemId(int itemId, LocalDateTime currentTime, Pageable pageable);

    // item next bookings
    @Query(value = "SELECT DISTINCT ON (item_id) * " +
            "FROM \"booking\" " +
            "WHERE item_id IN :itemIds " +
            "AND status = 'APPROVED' " +
            "AND start_time > :currentTime " +
            "ORDER BY item_id, start_time", nativeQuery = true)
    List<Booking> findNextBookingsByItemIds(Collection<Integer> itemIds, LocalDateTime currentTime);

    @Query("from Booking " +
            "where item.id = :itemId " +
            "and status = 'APPROVED' " +
            "and startTime > :currentTime")
    List<Booking> findNextBookingByItemId(int itemId, LocalDateTime currentTime, Pageable pageable);

    // comments
    boolean existsByBooker_IdAndItem_IdAndStatusAndEndTimeBefore(int bookerId, int itemId, Booking.Status status,
                                                                 LocalDateTime currentTime);
}
