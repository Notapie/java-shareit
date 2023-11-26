package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.IRRequestDto;
import ru.practicum.shareit.request.dto.IRResponseDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    @PostMapping
    public IRResponseDto addNewRequest(@RequestBody IRRequestDto irRequestDto,
                                       @RequestHeader(name = "X-Sharer-User-Id") int ownerId) {
        // TODO: add method body
        return null;
    }

    @GetMapping
    public Collection<IRResponseDto> getAllUserRequests(@RequestHeader(name = "X-Sharer-User-Id") int ownerId) {
        // TODO: add method body
        return  null;
    }

    @GetMapping("/all")
    public Collection<IRResponseDto> getAllRequests(@RequestParam int from, @RequestParam int size) {
        // TODO: add method body
        return null;
    }

    @GetMapping("/{requestId}")
    public IRResponseDto getRequest(@PathVariable int requestId) {
        // TODO: add method body
        return null;
    }
}
