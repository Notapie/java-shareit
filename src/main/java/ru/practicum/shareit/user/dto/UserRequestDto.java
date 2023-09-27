package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserRequestDto {
    String name;
    String email;
}
