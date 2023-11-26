package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.IRObjectMapper;
import ru.practicum.shareit.request.dto.IRRequestDto;
import ru.practicum.shareit.request.dto.IRResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService irService;

    @PostMapping
    public IRResponseDto addNewRequest(@RequestBody IRRequestDto irRequestDto,
                                       @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return IRObjectMapper.toResponseDto(irService.createNew(irRequestDto, ownerId));
    }

    @GetMapping
    public Collection<IRResponseDto> getAllUserRequests(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return IRObjectMapper.toResponseDto(irService.getAllByOwner(ownerId));
    }

    @GetMapping("/all")
    public Collection<IRResponseDto> getAllRequests(@RequestParam int from, @RequestParam int size) {
        return IRObjectMapper.toResponseDto(irService.getAll(from, size));
    }

    @GetMapping("/{requestId}")
    public IRResponseDto getRequest(@PathVariable int requestId) {
        return IRObjectMapper.toResponseDto(irService.getById(requestId));
    }
}
