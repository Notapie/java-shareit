package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.SaveException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.IRObjectMapper;
import ru.practicum.shareit.request.dto.IRRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestJpaRepository irRepository;

    public ItemRequest createNew(IRRequestDto irRequestDto, int ownerId) {
        // validate request description
        if (!StringUtils.hasText(irRequestDto.getDescription())) {
            throw new ValidationException("Item request description cannot be null or blank");
        }

        // get ItemRequest
        final ItemRequest itemRequest = IRObjectMapper.fromRequestDto(irRequestDto);

        // insert create time
        itemRequest.setCreated(LocalDateTime.now());

        // save new ItemRequest
        try {
            final ItemRequest savedItemRequest = irRepository.save(itemRequest);
            log.debug("Item request created. " + savedItemRequest);
            return savedItemRequest;
        } catch (Exception e) {
            throw new SaveException("Failed to save a new item request. " + itemRequest, e);
        }
    }

    public Collection<ItemRequest> getAllByOwner(int ownerId) {
        // TODO: add method body
        return null;
    }

    public Collection<ItemRequest> getAll(int fromIndex, int size) {
        // TODO: add method body
        return null;
    }

    public ItemRequest getById(int requestId) {
        // TODO: add method body
        return null;
    }
}
