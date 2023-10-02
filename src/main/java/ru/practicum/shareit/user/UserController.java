package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserObjectMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponseDto createUser(final @RequestBody UserRequestDto userRequestDto) {
        return UserObjectMapper.toUserResponseDto(userService.create(userRequestDto));
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(final @PathVariable int userId, final @RequestBody UserRequestDto userRequestDto) {
        return UserObjectMapper.toUserResponseDto(userService.update(userId, userRequestDto));
    }

    @DeleteMapping("/{userId}")
    public UserResponseDto deleteUserById(final @PathVariable int userId) {
        return UserObjectMapper.toUserResponseDto(userService.deleteById(userId));
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(final @PathVariable int userId) {
        return UserObjectMapper.toUserResponseDto(userService.getById(userId));
    }

    @GetMapping
    public Collection<UserResponseDto> getAllUsers() {
        return UserObjectMapper.toUserResponseDto(userService.getAll());
    }
}
