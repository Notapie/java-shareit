package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private ItemService itemService;

    @PostMapping
    public ItemDto createItem(final @RequestBody ItemDto itemDto,
                              final @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(final @PathVariable int itemId, final @RequestBody ItemDto itemDto,
                              final @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(final @PathVariable int itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllUserItems(final @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(final @RequestParam String text) {
        return itemService.search(text);
    }
}
