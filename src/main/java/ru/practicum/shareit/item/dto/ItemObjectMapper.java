package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemObjectMapper {
    public static Item fromItemRequestDto(ItemRequestDto itemRequestDto) {
        return Item.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .isAvailable(itemRequestDto.getAvailable())
                .build();
    }

    public static Item fromItemRequestDto(ItemRequestDto itemRequestDto, User owner) {
        return Item.builder()
                .owner(owner)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .isAvailable(itemRequestDto.getAvailable())
                .build();
    }

    public static ItemResponseDto toItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public static Collection<ItemResponseDto> toItemResponseDto(Collection<Item> items) {
        return items.stream()
                .map(ItemObjectMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    public static BookingShortResponseDto toBookingShortResponseDto(Booking booking) {
        return BookingShortResponseDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStartTime())
                .end(booking.getEndTime())
                .build();
    }

    public static ItemResponseExtendedDto toItemResponseExtendedDto(Item item) {
        return toItemResponseExtendedDto(item, null);
    }

    public static ItemResponseExtendedDto toItemResponseExtendedDto(Item item, Booking lastBooking, Booking nextBooking,
                                                                    Collection<Comment> comments) {
        ItemResponseExtendedDto.ItemResponseExtendedDtoBuilder builder = ItemResponseExtendedDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .comments(CommentObjectMapper.toResponseDto(comments));

        if (lastBooking != null) {
            builder.lastBooking(toBookingShortResponseDto(lastBooking));
        }
        if (nextBooking != null) {
            builder.nextBooking(toBookingShortResponseDto(nextBooking));
        }

        return builder.build();
    }

    public static ItemResponseExtendedDto toItemResponseExtendedDto(Item item, Collection<Comment> comments) {
        ItemResponseExtendedDto.ItemResponseExtendedDtoBuilder builder = ItemResponseExtendedDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable());

        if (comments != null) {
            builder.comments(CommentObjectMapper.toResponseDto(comments));
        }

        return builder.build();
    }
}
