package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.IRRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceJpa;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemRequestServiceIntegrationTest {
    private final UserServiceJpa userService;
    private final ItemRequestService irService;

    @Test
    @DisplayName("should success add new item request")
    public void shouldSuccessAddNewItemRequest() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final ItemRequest itemRequest = irService.createNew(new IRRequestDto("IR desc"), owner.getId());

        Assertions.assertEquals(owner.getId(), itemRequest.getOwner().getId());
        Assertions.assertEquals(owner.getName(), itemRequest.getOwner().getName());
        Assertions.assertEquals(owner.getEmail(), itemRequest.getOwner().getEmail());
    }
}
