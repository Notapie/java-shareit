package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserObjectMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.service.EmailValidator;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class UserServiceJpa implements UserService {
    private final EmailValidator emailValidator;
    private final UserJpaRepository userRepository;

    @Override
    public User create(UserRequestDto userRequestDto) {
        log.debug("Create user request. " + userRequestDto);
        // validate data
        validateToCreate(userRequestDto);

        // create user from dto
        final User user = UserObjectMapper.fromUserRequestDto(userRequestDto);

        // save new user
        final User savedUser = userRepository.save(user);
        log.debug("User created with. " + savedUser);

        return savedUser;
    }

    @Override
    public User update(int userId, UserRequestDto userRequestDto) {
        return null;
    }

    @Override
    public User deleteById(int userId) {
        return null;
    }

    @Override
    public User getById(int userId) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
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
