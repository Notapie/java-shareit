package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceJpa;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceIntegrationTest {
    private final ItemServiceJpa itemService;
    private final UserServiceJpa userService;

    @Test
    @DisplayName("should success create new item")
    public void shouldCreateNewItem() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final Item item = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        Assertions.assertEquals(owner.getId(), item.getOwner().getId());
        Assertions.assertEquals(owner.getName(), item.getOwner().getName());
        Assertions.assertEquals(owner.getEmail(), item.getOwner().getEmail());
    }
}
