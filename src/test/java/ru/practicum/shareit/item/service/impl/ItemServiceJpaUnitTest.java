package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.impl.BookingJpaUtil;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.service.ItemRequestJpaUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserJpaUtil;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ItemServiceJpaUnitTest {
    @Mock
    ItemJpaRepository itemRepositoryMock;

    @Mock
    CommentJpaRepository commentRepositoryMock;

    @Mock
    ItemJpaUtil itemUtilMock;

    @Mock
    UserJpaUtil userUtilMock;

    @Mock
    BookingJpaUtil bookingUtilMock;

    @Mock
    ItemRequestJpaUtil itemRequestUtilMock;

    @InjectMocks
    ItemServiceJpa itemService;

    @Test
    @DisplayName("should success create new item")
    public void shouldSuccessCreateNewItem() {
        final User owner = new User(1, "owner", "item owner");
        final ItemRequestDto itemRequestDto = new ItemRequestDto("item", "desc", true,
                null);
        final Item expectedSavedItem = new Item(1, itemRequestDto.getName(), itemRequestDto.getDescription(),
                itemRequestDto.getAvailable(), owner, null);

        when(userUtilMock.requireFindById(owner.getId()))
                .thenReturn(owner);
        when(itemRepositoryMock.save(any()))
                .thenAnswer(invocationOnMock -> {
                    final Item itemParam = invocationOnMock.getArgument(0, Item.class);
                    return itemParam.toBuilder().id(expectedSavedItem.getId()).build();
                });

        final Item savedItem = itemService.create(itemRequestDto, owner.getId());
        Assertions.assertEquals(expectedSavedItem.getId(), savedItem.getId());
        Assertions.assertEquals(expectedSavedItem.getOwner().getId(), savedItem.getOwner().getId());
    }
}
