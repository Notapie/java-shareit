package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.IRRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.impl.UserJpaUtil;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceUnitTest {
    @Mock
    ItemRequestJpaRepository irRepositoryMock;

    @Mock
    UserJpaUtil userUtilMock;

    @Mock
    ItemRequestJpaUtil irUtilMock;

    @InjectMocks
    ItemRequestService irService;

    @Test
    @DisplayName("should success add new item request")
    public void shouldSuccessCreateNewItemRequest() {
        final User owner = new User(1, "owner", "request owner");
        final LocalDateTime currentTime = LocalDateTime.now();
        final IRRequestDto irRequestDto = new IRRequestDto(
                "request desc"
        );
        final ItemRequest expectedSavedIR = new ItemRequest(
                1,
                irRequestDto.getDescription(),
                currentTime,
                owner,
                null
        );

        when(userUtilMock.requireFindById(owner.getId()))
                .thenReturn(owner);
        when(irRepositoryMock.save(any()))
                .thenAnswer(invocationOnMock -> {
                    final ItemRequest irParam = invocationOnMock.getArgument(0, ItemRequest.class);
                    return irParam.toBuilder().id(expectedSavedIR.getId()).build();
                });

        final ItemRequest savedItemRequest = irService.createNew(irRequestDto, owner.getId());
        Assertions.assertEquals(expectedSavedIR.getId(), savedItemRequest.getId());
        Assertions.assertEquals(expectedSavedIR.getOwner().getId(), savedItemRequest.getOwner().getId());
    }
}
