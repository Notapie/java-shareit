package ru.practicum.shareit.user.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Service
public class UserServiceInMemoryImpl implements UserService {

    @Override
    public UserDto create(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto update(int userId, UserDto userDto) {
        return null;
    }

    @Override
    public UserDto deleteById(int userId) {
        return null;
    }

    @Override
    public UserDto getById(int userId) {
        return null;
    }

    @Override
    public Collection<UserDto> getAll() {
        return null;
    }
}
