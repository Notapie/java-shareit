package ru.practicum.shareit.user.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
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
    public UserResponseDto create(UserRequestDto userRequestDto) {
        validateToCreate(userRequestDto);
        return null;
    }

    @Override
    public UserResponseDto update(int userId, UserRequestDto userRequestDto) {
        return null;
    }

    @Override
    public UserResponseDto deleteById(int userId) {
        return null;
    }

    @Override
    public UserResponseDto getById(int userId) {
        return null;
    }

    @Override
    public Collection<UserResponseDto> getAll() {
        return null;
    }

    private void validateToCreate(UserRequestDto userRequestDto) {
        if (userRequestDto.getEmail() == null) {
            throw new ValidationException("User email is null");
        }
        emailValidate(userRequestDto.getEmail());
    }

    private void emailValidate(String email) {
        if (!emailValidator.validate(email)) {
            throw new ValidationException("Invalid email");
        }
    }
}
