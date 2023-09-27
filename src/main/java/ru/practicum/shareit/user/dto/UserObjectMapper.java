package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserObjectMapper {
    public static User fromUserRequestDto(UserRequestDto userRequestDto) {
        return User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();
    }

    public static UserResponseDto toUserResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static Collection<UserResponseDto> toUserResponseDto(Collection<User> users) {
        return users.stream()
                .map(UserObjectMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }
}
