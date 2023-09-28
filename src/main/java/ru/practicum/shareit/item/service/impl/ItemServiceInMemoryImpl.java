package ru.practicum.shareit.item.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemObjectMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ItemServiceInMemoryImpl implements ItemService {
    private final UserService userService;
    private final Map<Integer, Item> idToItem;
    private final Map<Integer, Map<Integer, Item>> userIdToItems;

    public ItemServiceInMemoryImpl(UserService userService) {
        this.userService = userService;
        this.idToItem = new HashMap<>();
        this.userIdToItems = new HashMap<>();
    }

    @Override
    public Item create(ItemRequestDto itemRequestDto, int userId) {
        // validate input data
        validateToCreate(itemRequestDto);

        // find owner
        final User owner = userService.getById(userId);

        // get new Item object
        final Item item = ItemObjectMapper.fromItemRequestDto(itemRequestDto, userId);
        return null;
    }

    @Override
    public Item update(int userId, int itemId, ItemRequestDto itemRequestDto) {
        return null;
    }

    @Override
    public Item getById(int id) {
        return null;
    }

    @Override
    public Collection<Item> getAllUserItems(int userId) {
        return null;
    }

    @Override
    public Collection<Item> search(String query) {
        return null;
    }

    private void validateToCreate(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getAvailable() == null) {
            throw new ValidationException("Available property cannot is null");
        }

        if (!StringUtils.hasText(itemRequestDto.getName())) {
            throw new ValidationException("Item name cannot be null or blank");
        }

        if (!StringUtils.hasText(itemRequestDto.getDescription())) {
            throw new ValidationException("Item description cannot be null or blank");
        }

    }
}
