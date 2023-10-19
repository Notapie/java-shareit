package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommentObjectMapper {
    public static Comment fromCommentRequestDto(CommentRequestDto commentRequestDto, User author, Item item,
                                                LocalDateTime createdAt) {
        return Comment.builder()
                .author(author)
                .item(item)
                .creationTime(createdAt)
                .build();
    }

    public static CommentResponseDto toResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreationTime())
                .text(comment.getText())
                .build();
    }

    public static Collection<CommentResponseDto> toResponseDto(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentObjectMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
