package ru.practicum.shareit.user.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserObjectMapper;
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
    private int idCounter;

    public UserServiceInMemoryImpl(EmailValidator emailValidator) {
        this.idToUser = new HashMap<>();
        this.emailToUser = new HashMap<>();
        this.emailValidator = emailValidator;
        this.idCounter = 1;
    }

    @Override
    public UserResponseDto create(UserRequestDto userRequestDto) {
        // validate data
        validateToCreate(userRequestDto);

        // check email duplicates
        if (emailToUser.containsKey(userRequestDto.getEmail())) {
            throw new AlreadyExistsException("User with email " + userRequestDto.getEmail() + " already exists");
        }

        // create user from dto
        final User user = UserObjectMapper.fromUserRequestDto(userRequestDto, idCounter++);

        // insert new user into storage
        idToUser.put(user.getId(), user);
        emailToUser.put(user.getEmail(), user);

        // return created user
        return UserObjectMapper.toUserResponseDto(user);
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
