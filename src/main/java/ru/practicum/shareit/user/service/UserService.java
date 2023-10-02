package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User create(UserRequestDto userRequestDto);

    User update(int userId, UserRequestDto userRequestDto);

    User deleteById(int userId);

    User getById(int userId);

    Collection<User> getAll();
}
