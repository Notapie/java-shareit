package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SaveException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemObjectMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class ItemServiceJpa implements ItemService {
    private final ItemJpaRepository itemRepository;
    private final UserService userService;

    @Override
    public Item create(ItemRequestDto itemRequestDto, int userId) {
        log.debug("Create item request. " + itemRequestDto);

        // validate input data
        validateToCreate(itemRequestDto);

        // find owner
        final User owner = userService.getById(userId);

        // build new item entity
        final Item item = ItemObjectMapper.fromItemRequestDto(itemRequestDto, owner);

        // save new item
        try {
            Item savedItem = itemRepository.save(item);
            log.debug("Item created." + item);
            return savedItem;
        } catch (Exception e) {
            throw new SaveException("Failed to save a new item" + item, e);
        }
    }

    @Override
    public Item update(int userId, int itemId, ItemRequestDto itemRequestDto) {
        log.debug("Update item by id = " + itemId + " request from user id = " + userId + ". " + itemRequestDto);

        // get item
        Item item = requireFindById(itemId);

        // check if user is owner
        if (item.getOwner().getId() != userId) {
            throw new ForbiddenException("User with id " + userId + " is not the item owner");
        }

        // update item columns
        final String name = itemRequestDto.getName();
        if (StringUtils.hasText(name)) {
            item.setName(name);
        }

        final String description = itemRequestDto.getDescription();
        if (StringUtils.hasText(description)) {
            item.setDescription(description);
        }

        final Boolean isAvailable = itemRequestDto.getAvailable();
        if (isAvailable != null) {
            item.setIsAvailable(isAvailable);
        }

        // save updated item
        try {
            Item savedItem = itemRepository.save(item);
            log.debug("Item updated." + item);
            return savedItem;
        } catch (Exception e) {
            throw new SaveException("Failed to save updated item" + item, e);
        }
    }

    @Override
    public Item getById(int itemId) {
        log.debug("Get item by id = " + itemId + " request");
        return requireFindById(itemId);
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

    private Item requireFindById(int itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }
        return itemRepository.getReferenceById(itemId);
    }
}
