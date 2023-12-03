package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.impl.BookingJpaUtil;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseExtendedDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.service.ItemRequestJpaUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserJpaUtil;

import java.time.LocalDateTime;
import java.util.List;

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

    final int ownerId = 1;
    final int bookerId = 2;
    final int itemId = 1;
    final int commentId = 1;
    final int lastBookingId = 1;
    final int nextBookingId = 2;
    final User owner = new User(ownerId, "owner", "item owner");
    final User booker = new User(bookerId, "booker", "item booker");
    final ItemRequestDto itemRequestDto = new ItemRequestDto("item", "desc", true,
            null);
    final Item expectedSavedItem = new Item(itemId, itemRequestDto.getName(), itemRequestDto.getDescription(),
            itemRequestDto.getAvailable(), owner, null);
    final Comment expectedComment = Comment.builder()
            .id(commentId)
            .author(booker)
            .item(expectedSavedItem)
            .text("comment text")
            .creationTime(LocalDateTime.now())
            .build();
    final CommentRequestDto commentRequestDto = CommentRequestDto.builder()
            .text(expectedComment.getText())
            .build();
    final Booking expectedLastBooking = Booking.builder()
            .id(lastBookingId)
            .item(expectedSavedItem)
            .booker(booker)
            .status(Booking.Status.APPROVED)
            .build();
    final Booking expectedNextBooking = Booking.builder()
            .id(nextBookingId)
            .item(expectedSavedItem)
            .booker(booker)
            .status(Booking.Status.WAITING)
            .build();

    @Test
    @DisplayName("should success create new item")
    public void shouldSuccessCreateNewItem() {
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

    @Test
    @DisplayName("should success update item")
    public void updateItem() {
        final Item updatedExpectedItem = expectedSavedItem.toBuilder()
                .name("updated name")
                .description("updated desc")
                .isAvailable(false)
                .build();

        when(itemUtilMock.requireFindById(itemId))
                .thenReturn(expectedSavedItem);
        when(itemRepositoryMock.save(any()))
                .thenReturn(updatedExpectedItem);

        final Item result = itemService.update(ownerId, itemId, itemRequestDto.toBuilder()
                .name("updated name")
                .description("updated desc")
                .available(false)
                .build());

        Assertions.assertEquals(updatedExpectedItem.getId(), result.getId());
        Assertions.assertEquals(updatedExpectedItem.getName(), result.getName());
    }

    @Test
    @DisplayName("should success add new comment")
    public void addComment() {
        when(itemUtilMock.requireFindById(itemId))
                .thenReturn(expectedSavedItem);
        when(userUtilMock.requireFindById(bookerId))
                .thenReturn(booker);
        when(bookingUtilMock.isUserHasBookedItem(bookerId, itemId))
                .thenReturn(true);
        when(commentRepositoryMock.save(any()))
                .thenReturn(expectedComment);

        final Comment result = itemService.addComment(commentRequestDto, itemId, bookerId);

        Assertions.assertEquals(commentId, result.getId());
        Assertions.assertEquals(expectedComment.getText(), result.getText());
    }

    @Test
    @DisplayName("should success get item by id")
    public void getById() {
        when(itemUtilMock.requireFindById(itemId))
                .thenReturn(expectedSavedItem);
        when(commentRepositoryMock.findCommentsByItem_Id(itemId))
                .thenReturn(List.of());
        when(bookingUtilMock.getLastItemBooking(itemId))
                .thenReturn(null);
        when(bookingUtilMock.getNextItemBooking(itemId))
                .thenReturn(null);

        final ItemResponseExtendedDto result = itemService.getById(itemId, ownerId);

        Assertions.assertEquals(itemId, result.getId());
        Assertions.assertEquals(expectedSavedItem.getName(), result.getName());
    }

    @Test
    @DisplayName("should success get all owner items")
    public void getAllOwnerItems() {
        when(itemRepositoryMock.findItemsByOwnerIdIs(ownerId))
                .thenReturn(List.of(expectedSavedItem));
        when(bookingUtilMock.getLastItemsBookings(any()))
                .thenReturn(List.of(expectedLastBooking));
        when(bookingUtilMock.getNextItemsBookings(any()))
                .thenReturn(List.of(expectedNextBooking));
        when(commentRepositoryMock.findCommentsByItem_IdIn(any()))
                .thenReturn(List.of(expectedComment));

        final ItemResponseExtendedDto result = itemService.getAllUserItems(ownerId).iterator().next();

        Assertions.assertEquals(itemId, result.getId());
        Assertions.assertEquals(lastBookingId, result.getLastBooking().getId());
        Assertions.assertEquals(nextBookingId, result.getNextBooking().getId());
        Assertions.assertEquals(commentId, result.getComments().iterator().next().getId());
    }

    @Test
    @DisplayName("should success search items by query")
    public void searchByTextQuery() {
        when(itemRepositoryMock.searchItemsByQuery(any()))
                .thenReturn(List.of(expectedSavedItem));

        final Item result = itemService.search("test").iterator().next();

        Assertions.assertEquals(itemId, result.getId());
    }
}
