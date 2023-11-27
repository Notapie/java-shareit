package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.SaveException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.IRObjectMapper;
import ru.practicum.shareit.request.dto.IRRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserJpaUtil;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestJpaRepository irRepository;
    private final UserJpaUtil userUtil;
    private final ItemRequestJpaUtil irUtil;

    public ItemRequest createNew(IRRequestDto irRequestDto, int ownerId) {
        log.debug("Item request creation request. Owner id: " + ownerId + ", " + irRequestDto);
        // validate request description
        if (!StringUtils.hasText(irRequestDto.getDescription())) {
            throw new ValidationException("Item request description cannot be null or blank");
        }

        // find owner by id
        final User owner = userUtil.requireFindById(ownerId);

        // get ItemRequest
        final ItemRequest itemRequest = IRObjectMapper.fromRequestDto(irRequestDto, owner);

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
        log.debug("Get all item requests by owner. Owner id " + ownerId);

        // get owner
        userUtil.requireFindById(ownerId);

        // get owner requests
        Collection<ItemRequest> requests = irRepository.findItemRequestsByOwner_Id(ownerId);

        log.debug("Found " + requests.size() + " requests");
        return requests;
    }

    public Collection<ItemRequest> getAll(int userId, int fromIndex, int size) {
        if (fromIndex < 0 || size <= 0) {
            throw new ValidationException("From or size params cannot be negative, size cannot be 0");
        }
        log.debug("Get all item requests. FromIndex " + fromIndex + ", size " + size);

        // get user
        userUtil.requireFindById(userId);

        // get all other users requests
        Collection<ItemRequest> requests = irRepository.findAllItemRequestsExcludeOwner(
                userId,
                PageRequest.of(fromIndex / size, size, Sort.by("created").descending())
        );

        log.debug("Found " + requests.size() + " requests");
        return requests;
    }

    public ItemRequest getById(int userId, int requestId) {
        log.debug("Get item request by id " + requestId + " from user with id " + userId);

        // get user
        userUtil.requireFindById(userId);

        // get request
        return irUtil.requireFindById(requestId);
    }
}
