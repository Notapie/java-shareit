package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

@Component
@RequiredArgsConstructor
public class UserJpaUtil {
    private final UserJpaRepository userRepository;

    public void assertExists(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }

    public User requireFindById(int userId) {
        assertExists(userId);
        return userRepository.getReferenceById(userId);
    }
}
