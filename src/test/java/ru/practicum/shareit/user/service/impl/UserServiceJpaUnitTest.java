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

    @Test
    @DisplayName("should success create new user")
    public void shouldSuccessCreateNewUser() {
        final UserRequestDto userRequestDto = new UserRequestDto("user name", "user@email.com");
        final User expectedSavedUser = new User(1, userRequestDto.getEmail(), userRequestDto.getName());

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
}
