package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping
    public UserResponseDto createUser(final @RequestBody UserRequestDto userRequestDto) {
        return userService.create(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(final @PathVariable int userId, final @RequestBody UserRequestDto userRequestDto) {
        return userService.update(userId, userRequestDto);
    }

    @DeleteMapping("/{userId}")
    public UserResponseDto deleteUserById(final @PathVariable int userId) {
        return userService.deleteById(userId);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(final @PathVariable int userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public Collection<UserResponseDto> getAllUsers() {
        return userService.getAll();
    }
}
