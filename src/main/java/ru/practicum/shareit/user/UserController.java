package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping
    public UserDto createUser(final @RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(final @PathVariable int userId, final @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUserById(final @PathVariable int userId) {
        return userService.deleteById(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(final @PathVariable int userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        return userService.getAll();
    }
}
