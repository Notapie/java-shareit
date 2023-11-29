package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Builder
@RequiredArgsConstructor
public class UserRequestDto {
    String name;
    String email;
}
