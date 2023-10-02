package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemObjectMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(final @RequestBody ItemRequestDto itemRequestDto,
                                      final @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return ItemObjectMapper.toItemResponseDto(itemService.create(itemRequestDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(final @PathVariable int itemId, final @RequestBody ItemRequestDto itemRequestDto,
                                     final @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return ItemObjectMapper.toItemResponseDto(itemService.update(userId, itemId, itemRequestDto));
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(final @PathVariable int itemId) {
        return ItemObjectMapper.toItemResponseDto(itemService.getById(itemId));
    }

    @GetMapping
    public Collection<ItemResponseDto> getAllUserItems(final @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return ItemObjectMapper.toItemResponseDto(itemService.getAllUserItems(userId));
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> searchItem(final @RequestParam String text) {
        return ItemObjectMapper.toItemResponseDto(itemService.search(text));
    }
}
