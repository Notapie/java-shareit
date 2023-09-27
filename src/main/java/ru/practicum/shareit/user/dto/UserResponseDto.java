package ru.practicum.shareit.user.dto;

import lombok.Value;

@Value
public class UserResponseDto {
    Integer id;
    String name;
    String email;
}
