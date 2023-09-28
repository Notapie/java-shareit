package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemRequestDto createItem(final @RequestBody ItemRequestDto itemRequestDto,
                                     final @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.create(itemRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemRequestDto updateItem(final @PathVariable int itemId, final @RequestBody ItemRequestDto itemRequestDto,
                                     final @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.update(userId, itemId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ItemRequestDto getItemById(final @PathVariable int itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllUserItems(final @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemRequestDto> searchItem(final @RequestParam String text) {
        return itemService.search(text);
    }
}
