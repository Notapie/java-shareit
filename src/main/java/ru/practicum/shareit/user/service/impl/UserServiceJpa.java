package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SaveException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserObjectMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.service.EmailValidator;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
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
        try {
            final User savedUser = userRepository.save(user);
            log.debug("User created. " + savedUser);

            return savedUser;
        } catch (Exception e) {
            throw new SaveException("Failed to create a new user. " + user, e);
        }
    }

    @Override
    @Transactional
    public User update(int userId, UserRequestDto userRequestDto) {
        log.debug("Update user by id = " + userId + " request. " + userRequestDto);

        try {
            // getting user from storage
            final User user = userRepository.getReferenceById(userId);

            // update email
            final String newEmail = userRequestDto.getEmail();
            if (newEmail != null && !newEmail.equals(user.getEmail())) {
                emailValidate(newEmail);
                user.setEmail(newEmail);
            }

            // update name
            final String newName = userRequestDto.getName();
            if (newName != null) {
                user.setName(newName);
            }

            // save and return
            final User savedUser = userRepository.save(user);
            log.debug("User updated. " + savedUser);
            return savedUser;
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("User with id " + userId + " not found");
        } catch (Exception e) {
            throw new SaveException("Failed to update user with id " + userId, e);
        }
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
