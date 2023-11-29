package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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

        Assertions.assertEquals(user.getId(), 1);
        Assertions.assertEquals(user.getName(), userRequestDto.getName());
        Assertions.assertEquals(user.getEmail(), userRequestDto.getEmail());
    }
}
