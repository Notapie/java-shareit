package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.Collection;

public interface UserService {
    UserResponseDto create(UserRequestDto userRequestDto);
    UserResponseDto update(int userId, UserRequestDto userRequestDto);
    UserResponseDto deleteById(int userId);
    UserResponseDto getById(int userId);
    Collection<UserResponseDto> getAll();
}
