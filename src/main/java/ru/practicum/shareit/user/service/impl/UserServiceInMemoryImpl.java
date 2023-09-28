package ru.practicum.shareit.user.service.impl;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserObjectMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
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
    public User create(UserRequestDto userRequestDto) {
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
        return user;
    }

    @Override
    public User update(int userId, UserRequestDto userRequestDto) {
        // getting user from storage
        final User user = requireFindById(userId);

        // if updating email, check if new email already exists and update builder
        final User.UserBuilder userBuilder = user.toBuilder();
        final String newEmail = userRequestDto.getEmail();
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (emailToUser.containsKey(newEmail)) {
                throw new AlreadyExistsException("User with email " + newEmail + " already exists");
            }
            emailValidate(newEmail);
            userBuilder.email(newEmail);
        }

        // update name in builder
        final String newName = userRequestDto.getName();
        if (newName != null) {
            userBuilder.name(newName);
        }

        // update user object in storage
        final User updatedUser = userBuilder.build();

        idToUser.put(updatedUser.getId(), updatedUser);
        emailToUser.put(updatedUser.getEmail(), updatedUser);

        // and remove old email mapping
        if (!updatedUser.getEmail().equals(user.getEmail())) {
            emailToUser.remove(user.getEmail());
        }

        return updatedUser;
    }

    @Override
    public User deleteById(int userId) {
        final User user = requireFindById(userId);

        idToUser.remove(user.getId());
        emailToUser.remove(user.getEmail());

        return user;
    }

    @Override
    public User getById(int userId) {
        return requireFindById(userId);
    }

    @Override
    public Collection<User> getAll() {
        return idToUser.values();
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

    private User requireFindById(int userId) {
        final User user = idToUser.get(userId);
        if (user == null) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        return user;
    }
}
