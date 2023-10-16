package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemObjectMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseExtendedDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(@RequestBody ItemRequestDto itemRequestDto,
                                      @RequestHeader(name = "X-Sharer-User-Id") final int userId) {
        return ItemObjectMapper.toItemResponseDto(itemService.create(itemRequestDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@PathVariable int itemId, @RequestBody ItemRequestDto itemRequestDto,
                                     @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return ItemObjectMapper.toItemResponseDto(itemService.update(userId, itemId, itemRequestDto));
    }

    @GetMapping("/{itemId}")
    public ItemResponseExtendedDto getItemById(@PathVariable int itemId, @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemResponseExtendedDto> getAllUserItems(@RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemResponseDto> searchItem(@RequestParam String text) {
        return ItemObjectMapper.toItemResponseDto(itemService.search(text));
    }
}
