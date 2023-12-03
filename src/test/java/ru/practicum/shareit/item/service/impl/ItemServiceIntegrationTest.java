package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseExtendedDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.IRRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserServiceJpa;

import java.time.LocalDateTime;
import java.util.Collection;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceIntegrationTest {
    private final ItemServiceJpa itemService;
    private final UserServiceJpa userService;
    private final BookingService bookingService;
    private final ItemRequestService irService;

    @Test
    @DisplayName("should success create new item")
    public void shouldCreateNewItem() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));
        final ItemRequest ir = irService.createNew(IRRequestDto.builder().description("desc").build(), booker.getId());
        final Item item = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, ir.getId()), owner.getId());

        Assertions.assertEquals(owner.getId(), item.getOwner().getId());
        Assertions.assertEquals(owner.getName(), item.getOwner().getName());
        Assertions.assertEquals(owner.getEmail(), item.getOwner().getEmail());
    }

    @Test
    @DisplayName("should update item")
    public void updateItem() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final Item item = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());
        final Item updatedItem = itemService.update(owner.getId(),
                item.getId(), ItemRequestDto.builder().name("updated name").build());

        Assertions.assertEquals("updated name", updatedItem.getName());
    }

    @Test
    @DisplayName("should add comment")
    public void addComment() throws InterruptedException {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User booker = userService.create(new UserRequestDto("booker", "booker@yandex.ru"));
        final Item item = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        final Booking booking = bookingService.create(BookingRequestDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .build(), booker.getId());

        bookingService.approve(booking.getId(), owner.getId(), true);

        Thread.sleep(2000);

        final Comment comment = itemService.addComment(CommentRequestDto.builder()
                .text("comment text")
                .build(), item.getId(), booker.getId());

        Assertions.assertEquals("comment text", comment.getText());
    }

    @Test
    @DisplayName("should get item by id")
    public void getItemById() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User user = userService.create(new UserRequestDto("user", "user@yandex.ru"));
        final Item item = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        final ItemResponseExtendedDto resultOwner = itemService.getById(item.getId(), owner.getId());
        final ItemResponseExtendedDto result = itemService.getById(item.getId(), user.getId());

        Assertions.assertEquals(item.getId(), result.getId());
        Assertions.assertEquals(item.getId(), resultOwner.getId());
    }

    @Test
    @DisplayName("should get all user items")
    public void getAllOwnerItems() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final Item item = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        final ItemResponseExtendedDto result = itemService.getAllUserItems(owner.getId()).iterator().next();

        Assertions.assertEquals(item.getId(), result.getId());
    }

    @Test
    @DisplayName("should search items by text query")
    public void searchByTextQuery() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final Item item = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        final Collection<Item> result = itemService.search("test");
        Assertions.assertEquals(item.getName(), result.iterator().next().getName());

        final Collection<Item> emptyResult = itemService.search("");
        Assertions.assertEquals(0, emptyResult.size());
    }

    // exceptions

    @Test
    @DisplayName("should error if update non owner")
    public void errorIfNonOwner() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User user = userService.create(new UserRequestDto("user", "user@yandex.ru"));
        final Item item = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        Assertions.assertThrows(ForbiddenException.class, () -> {
            itemService.update(user.getId(),
                    item.getId(), ItemRequestDto.builder().name("updated name").build());
        });
    }

    @Test
    @DisplayName("should error if invalid create")
    public void invalidCreate() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));

        // available is null
        Assertions.assertThrows(ValidationException.class, () -> {
            itemService.create(new ItemRequestDto("test item", "test item desc",
                    null, null), owner.getId());
        });

        // name is null
        Assertions.assertThrows(ValidationException.class, () -> {
            itemService.create(new ItemRequestDto(null, "test item desc",
                    true, null), owner.getId());
        });

        // desc is null
        Assertions.assertThrows(ValidationException.class, () -> {
            itemService.create(new ItemRequestDto("test item", null,
                    true, null), owner.getId());
        });
    }

    @Test
    @DisplayName("should error if comment invalid create")
    public void commentInvalidCreate() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        final User user = userService.create(new UserRequestDto("user", "user@yandex.ru"));
        final Item item = itemService.create(new ItemRequestDto("test item", "test item desc",
                true, null), owner.getId());

        // comment text is null
        Assertions.assertThrows(ValidationException.class, () -> {
            itemService.addComment(CommentRequestDto.builder()
                    .text(null)
                    .build(), item.getId(), 123);
        });

        // user never booked item
        Assertions.assertThrows(ValidationException.class, () -> {
            itemService.addComment(CommentRequestDto.builder()
                    .text("123")
                    .build(), item.getId(), user.getId());
        });
    }

    @Test
    @DisplayName("should error if item not found")
    public void itemNotFound() {
        final User owner = userService.create(new UserRequestDto("owner", "owner@yandex.ru"));
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.getById(12, owner.getId());
        });
    }
}
