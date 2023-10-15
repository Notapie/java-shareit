package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {}
