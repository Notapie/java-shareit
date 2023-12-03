package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SaveException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceIntegrationTest {
    private final UserServiceJpa userService;

    @Test
    @DisplayName("should success create new user")
    public void shouldSuccessCreateNewUser() {
        final UserRequestDto userRequestDto = new UserRequestDto("owner", "owner@yandex.ru");
        final User user = userService.create(userRequestDto);

        Assertions.assertEquals(user.getName(), userRequestDto.getName());
        Assertions.assertEquals(user.getEmail(), userRequestDto.getEmail());
    }

    @Test
    @DisplayName("should update user")
    public void updateUser() {
        final UserRequestDto userRequestDto = new UserRequestDto("owner", "owner@yandex.ru");
        final User user = userService.create(userRequestDto);

        final UserRequestDto updateRequest = UserRequestDto.builder()
                .name("owner updated")
                .build();

        final User updatedUser = userService.update(user.getId(), updateRequest);

        Assertions.assertEquals(user.getId(), updatedUser.getId());
        Assertions.assertEquals(user.getName(), "owner updated");
    }

    @Test
    @DisplayName("should delete by id")
    public void deleteUserById() {
        final UserRequestDto userRequestDto = new UserRequestDto("owner", "owner@yandex.ru");
        final User user = userService.create(userRequestDto);

        final User deleted = userService.deleteById(user.getId());

        Assertions.assertEquals(user.getId(), deleted.getId());
    }

    @Test
    @DisplayName("should get by id")
    public void getById() {
        final UserRequestDto userRequestDto = new UserRequestDto("owner", "owner@yandex.ru");
        final User user = userService.create(userRequestDto);

        final User result = userService.getById(user.getId());

        Assertions.assertEquals(user.getId(), result.getId());
    }

    @Test
    @DisplayName("should get all users")
    public void getAll() {
        final UserRequestDto userRequestDto = new UserRequestDto("owner", "owner@yandex.ru");
        final User user = userService.create(userRequestDto);

        final User result = userService.getAll().iterator().next();

        Assertions.assertEquals(user.getId(), result.getId());
    }

    // exceptions

    @Test
    @DisplayName("should error if create with invalid data")
    public void invalidCreate() {
        // invalid email (validation exception)
        Assertions.assertThrows(ValidationException.class, () -> {
            final UserRequestDto userRequestDto = new UserRequestDto("owner", "owner");
            final User user = userService.create(userRequestDto);
        });

        // null email (validation exception)
        Assertions.assertThrows(ValidationException.class, () -> {
            final UserRequestDto userRequestDto = new UserRequestDto("owner", null);
            final User user = userService.create(userRequestDto);
        });

        // double creating (save exception)
        final UserRequestDto userRequestDto = new UserRequestDto("owner", "owner@asdasd.ru");
        final User user = userService.create(userRequestDto);
        Assertions.assertThrows(SaveException.class, () -> {
            userService.create(userRequestDto);
        });
    }

    @Test
    @DisplayName("should error if user not found")
    public void notFound() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.getById(1);
        });
    }
}
