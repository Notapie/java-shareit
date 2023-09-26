package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);
    UserDto update(int userId, UserDto userDto);
    UserDto deleteById(int userId);
    UserDto getById(int userId);
    Collection<UserDto> getAll();
}
