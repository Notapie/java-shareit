package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.service.EmailValidator;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceJpaUnitTest {
    @Mock
    UserJpaRepository userRepositoryMock;

    @Mock
    UserJpaUtil userUtilMock;

    @Mock
    EmailValidator emailValidatorMock;

    @InjectMocks
    UserServiceJpa userService;

    final UserRequestDto userRequestDto = new UserRequestDto("user name", "user@email.com");
    final User expectedSavedUser = new User(1, userRequestDto.getEmail(), userRequestDto.getName());

    @Test
    @DisplayName("should success create new user")
    public void shouldSuccessCreateNewUser() {
        when(emailValidatorMock.validate(userRequestDto.getEmail()))
                .thenReturn(true);
        when(userRepositoryMock.save(any()))
                .thenAnswer(invocationOnMock -> {
                    final User userParam = invocationOnMock.getArgument(0, User.class);
                    return userParam.toBuilder().id(expectedSavedUser.getId()).build();
                });

        final User savedUser = userService.create(userRequestDto);
        Assertions.assertEquals(expectedSavedUser.getId(), savedUser.getId());
        Assertions.assertEquals(expectedSavedUser.getName(), savedUser.getName());
        Assertions.assertEquals(expectedSavedUser.getEmail(), savedUser.getEmail());
    }

    @Test
    @DisplayName("should update user")
    public void updateUser() {
        when(userUtilMock.requireFindById(expectedSavedUser.getId()))
                .thenReturn(expectedSavedUser);
        final User updatedExpectedUser = expectedSavedUser.toBuilder()
                .email("new email")
                .name("new name")
                .build();
        when(userRepositoryMock.save(any()))
                .thenReturn(updatedExpectedUser);
        when(emailValidatorMock.validate(updatedExpectedUser.getEmail()))
                .thenReturn(true);

        final User result = userService.update(expectedSavedUser.getId(), userRequestDto.toBuilder()
                .email(updatedExpectedUser.getEmail())
                .name(updatedExpectedUser.getName())
                .build());

        Assertions.assertEquals(updatedExpectedUser.getId(), result.getId());
        Assertions.assertEquals(updatedExpectedUser.getEmail(), result.getEmail());
        Assertions.assertEquals(updatedExpectedUser.getName(), result.getName());
    }

    @Test
    @DisplayName("should delete user by id")
    public void deleteUserById() {
        when(userUtilMock.requireFindById(expectedSavedUser.getId()))
                .thenReturn(expectedSavedUser);

        final User result = userService.deleteById(expectedSavedUser.getId());

        Assertions.assertEquals(expectedSavedUser.getId(), result.getId());
    }

    @Test
    @DisplayName("should get user by id")
    public void getUserById() {
        when(userUtilMock.requireFindById(expectedSavedUser.getId()))
                .thenReturn(expectedSavedUser);

        final User result = userService.getById(expectedSavedUser.getId());

        Assertions.assertEquals(expectedSavedUser.getId(), result.getId());
    }

    @Test
    @DisplayName("should get all users")
    public void getAllUsers() {
        when(userRepositoryMock.findAll())
                .thenReturn(List.of(expectedSavedUser));

        final User result = userService.getAll().iterator().next();

        Assertions.assertEquals(expectedSavedUser.getId(), result.getId());
    }
}
