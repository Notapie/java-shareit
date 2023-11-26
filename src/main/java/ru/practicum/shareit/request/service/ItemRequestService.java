package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.IRRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestService {
    public ItemRequest createNew(IRRequestDto irRequestDto, int ownerId) {
        // TODO: add method body
        return null;
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
