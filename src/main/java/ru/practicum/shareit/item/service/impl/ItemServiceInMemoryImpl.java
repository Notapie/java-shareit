package ru.practicum.shareit.item.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemObjectMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ItemServiceInMemoryImpl implements ItemService {
    private final UserService userService;
    private final Map<Integer, Item> idToItem;
    private final Map<Integer, Map<Integer, Item>> userIdToItems;

    private int itemCounter;

    public ItemServiceInMemoryImpl(UserService userService) {
        this.userService = userService;
        this.idToItem = new HashMap<>();
        this.userIdToItems = new HashMap<>();
        this.itemCounter = 1;
    }

    @Override
    public Item create(ItemRequestDto itemRequestDto, int userId) {
        log.debug("Create item request from user id = " + userId + ". " + itemRequestDto);

        // validate input data
        validateToCreate(itemRequestDto);

        // find owner
        final User owner = userService.getById(userId);

        // get new Item object
        final Item item = ItemObjectMapper.fromItemRequestDto(itemRequestDto, userId, itemCounter++);

        // save new item
        idToItem.put(item.getId(), item);

        final Map<Integer, Item> idToUserItems = getUserItemsMap(userId);
        idToUserItems.put(item.getId(), item);

        log.debug("Item created." + item);

        return item;
    }

    @Override
    public Item update(int userId, int itemId, ItemRequestDto itemRequestDto) {
        log.debug("Update item by id = " + itemId + " request from user id = " + userId + ". " + itemRequestDto);

        // get item
        final Item item = requireFindById(itemId);

        // check if user is owner
        if (!item.getOwnerId().equals(userId)) {
            throw new ForbiddenException("User with id " + userId + " is not the item owner");
        }

        // build new item
        final Item.ItemBuilder itemBuilder = item.toBuilder();

        if (StringUtils.hasText(itemRequestDto.getName())) {
            itemBuilder.name(itemRequestDto.getName());
        }
        if (StringUtils.hasText(itemRequestDto.getDescription())) {
            itemBuilder.description(itemRequestDto.getDescription());
        }
        if (itemRequestDto.getAvailable() != null) {
            itemBuilder.isAvailable(itemRequestDto.getAvailable());
        }
        final Item newItem = itemBuilder.build();

        // save new item
        idToItem.put(newItem.getId(), newItem);
        getUserItemsMap(userId).put(newItem.getId(), newItem);

        log.debug("Item updated. " + item);

        return newItem;
    }

    @Override
    public Item getById(int itemId) {
        log.debug("Get item by id = " + itemId + " request");

        return requireFindById(itemId);
    }

    @Override
    public Collection<Item> getAllUserItems(int userId) {
        log.debug("Request to get all user items");

        return getUserItemsMap(userId).values();
    }

    @Override
    public Collection<Item> search(String query) {
        log.debug("Request to search available items by query \"" + query + "\"");

        if (!StringUtils.hasText(query)) {
            return Collections.emptyList();
        }

        // search
        final Map<Integer, Item> searchResult = new HashMap<>();
        for (final Item item : idToItem.values()) {
            if (!item.getIsAvailable()) {
                continue;
            }

            // check match
            if (item.getName().toLowerCase().contains(query.toLowerCase())
                    || item.getDescription().toLowerCase().contains(query.toLowerCase())) {
                searchResult.put(item.getId(), item);
            }
        }

        log.debug("Found " + searchResult.size() + " item(s)");

        return searchResult.values();
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

    private Map<Integer, Item> getUserItemsMap(int userId) {
        return userIdToItems.computeIfAbsent(userId, k -> new HashMap<>());
    }

    private Item requireFindById(int itemId) {
        final Item item = idToItem.get(itemId);
        if (item == null) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }
        return item;
    }
}
