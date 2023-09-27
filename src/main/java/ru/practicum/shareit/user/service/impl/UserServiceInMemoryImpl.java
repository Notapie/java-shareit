package ru.practicum.shareit.user.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.EmailValidator;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceInMemoryImpl implements UserService {
    private final EmailValidator emailValidator;
    private final Map<Integer, User> idToUser;
    private final Map<String, User> emailToUser;

    public UserServiceInMemoryImpl(EmailValidator emailValidator) {
        this.idToUser = new HashMap<>();
        this.emailToUser = new HashMap<>();
        this.emailValidator = emailValidator;
    }

    @Override
    public UserDto create(UserDto userDto) {
        validate(userDto);
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

    private void validate(UserDto userDto) {
        if (userDto.getId() == null) {
            throw new ValidationException("User id is null");
        }

        if (userDto.getEmail() == null) {
            throw new ValidationException("User email is null");
        }

        if (!emailValidator.validate(userDto.getEmail())) {
            throw new ValidationException("Invalid email");
        }
    }
}
