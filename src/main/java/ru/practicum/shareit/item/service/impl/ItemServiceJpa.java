package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.impl.BookingJpaUtil;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.SaveException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserJpaUtil;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class ItemServiceJpa implements ItemService {
    private final ItemJpaRepository itemRepository;
    private final CommentJpaRepository commentRepository;
    private final ItemJpaUtil itemUtil;
    private final UserJpaUtil userUtil;
    private final BookingJpaUtil bookingUtil;

    @Override
    public Item create(ItemRequestDto itemRequestDto, int userId) {
        log.debug("Create item request. " + itemRequestDto);

        // validate input data
        validateToCreate(itemRequestDto);

        // find owner
        final User owner = userUtil.requireFindById(userId);

        // build new item entity
        final Item item = ItemObjectMapper.fromItemRequestDto(itemRequestDto, owner);

        // save new item
        try {
            Item savedItem = itemRepository.save(item);
            log.debug("Item created." + item);
            return savedItem;
        } catch (Exception e) {
            throw new SaveException("Failed to save a new item" + item, e);
        }
    }

    @Override
    public Item update(int userId, int itemId, ItemRequestDto itemRequestDto) {
        log.debug("Update item by id = " + itemId + " request from user id = " + userId + ". " + itemRequestDto);

        // get item
        Item item = itemUtil.requireFindById(itemId);

        // check if user is owner
        if (item.getOwner().getId() != userId) {
            throw new ForbiddenException("User with id " + userId + " is not the item owner");
        }

        // update item columns
        final String name = itemRequestDto.getName();
        if (StringUtils.hasText(name)) {
            item.setName(name);
        }

        final String description = itemRequestDto.getDescription();
        if (StringUtils.hasText(description)) {
            item.setDescription(description);
        }

        final Boolean isAvailable = itemRequestDto.getAvailable();
        if (isAvailable != null) {
            item.setIsAvailable(isAvailable);
        }

        // save updated item
        try {
            Item savedItem = itemRepository.save(item);
            log.debug("Item updated. " + savedItem);
            return savedItem;
        } catch (Exception e) {
            throw new SaveException("Failed to save updated item" + item, e);
        }
    }

    @Override
    public Comment addComment(CommentRequestDto commentRequestDto, int itemId, int authorId) {
        log.debug("Create comment from user " + authorId + " for item " + itemId + " request. " + commentRequestDto);
        // validate request dto
        validateToCreate(commentRequestDto);

        // get item
        final Item item = itemUtil.requireFindById(itemId);

        // get author
        final User author = userUtil.requireFindById(authorId);

        // check if the author has booked the item
        if (!bookingUtil.isUserHasBookedItem(authorId, itemId)) {
            throw new ValidationException("The user has never booked this item");
        }

        // create comment entity
        final Comment comment = CommentObjectMapper.fromCommentRequestDto(
                commentRequestDto,
                author,
                item,
                LocalDateTime.now()
        );

        // save new comment
        try {
            final Comment savedComment = commentRepository.save(comment);
            log.debug("Comment created. " + savedComment);
            return savedComment;
        } catch (Exception e) {
            throw new SaveException("Failed to save new comment. " + comment, e);
        }
    }

    @Override
    public ItemResponseExtendedDto getById(int itemId, int userId) {
        log.debug("Get item by id = " + itemId + " request");
        final Item item = itemUtil.requireFindById(itemId);

        // get comments
        final Collection<Comment> comments = commentRepository.findCommentsByItem_Id(itemId);

        // if user is not the owner just return the item
        if (item.getOwner().getId() != userId) {
            return ItemObjectMapper.toItemResponseExtendedDto(item, comments);
        }

        // get last item booking
        final Booking lastBooking = bookingUtil.getLastItemBooking(itemId);

        // get nex item booking
        final Booking nextBooking = bookingUtil.getNextItemBooking(itemId);

        // return item with bookings
        return ItemObjectMapper.toItemResponseExtendedDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Collection<ItemResponseExtendedDto> getAllUserItems(int userId) {
        log.debug("Request to get all user " + userId + " items");
        List<Item> items = itemRepository.findItemsByOwnerIdIs(userId);

        // get item extended DTOs
        final Map<Integer, ItemResponseExtendedDto> itemIdToItemExtDto = new HashMap<>();
        for (Item item : items) {
            itemIdToItemExtDto.put(item.getId(), ItemObjectMapper.toItemResponseExtendedDto(item));
        }

        // get last bookings
        List<Booking> lastBookings = bookingUtil.getLastItemsBookings(itemIdToItemExtDto.keySet());

        // get next bookings
        List<Booking> nextBookings = bookingUtil.getNextItemsBookings(itemIdToItemExtDto.keySet());

        // get comments
        List<Comment> comments = commentRepository.findCommentsByItem_IdIn(itemIdToItemExtDto.keySet());

        final int size = Collections.max(List.of(lastBookings.size(), nextBookings.size(), comments.size()));
        for (int i = 0; i < size; ++i) {
            if (i < lastBookings.size()) {
                Booking lastBooking = lastBookings.get(i);
                ItemResponseExtendedDto dto = itemIdToItemExtDto.get(lastBooking.getItem().getId());
                dto.setLastBooking(ItemObjectMapper.toBookingShortResponseDto(lastBooking));
            }

            if (i < nextBookings.size()) {
                Booking nextBooking = nextBookings.get(i);
                ItemResponseExtendedDto dto = itemIdToItemExtDto.get(nextBooking.getItem().getId());
                dto.setNextBooking(ItemObjectMapper.toBookingShortResponseDto(nextBooking));
            }

            if (i < comments.size()) {
                Comment comment = comments.get(i);
                ItemResponseExtendedDto dto = itemIdToItemExtDto.get(comment.getItem().getId());

                Collection<CommentResponseDto> itemComments = dto.getComments();
                if (itemComments == null) {
                    itemComments = new ArrayList<>();
                }

                itemComments.add(CommentObjectMapper.toResponseDto(comment));
            }
        }

        return itemIdToItemExtDto.values();
    }

    @Override
    public Collection<Item> search(String query) {
        log.debug("Request to search available items by query \"" + query + "\"");
        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemsByQuery(query);
    }

    private void validateToCreate(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getAvailable() == null) {
            throw new ValidationException("Available property cannot is null");
        }

        if (!StringUtils.hasText(itemRequestDto.getName())) {
            throw new ValidationException("Item name cannot be null or blank");
        }

        if (!StringUtils.hasText(itemRequestDto.getDescription())) {
            throw new ValidationException("Item description cannot be null or blank");
        }
    }

    private void validateToCreate(CommentRequestDto commentRequestDto) {
        if (!StringUtils.hasText(commentRequestDto.getText())) {
            throw new ValidationException("Comment text cannot be null or blank");
        }
    }
}
