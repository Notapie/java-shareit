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
import java.util.List;

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

    @Test
    @DisplayName("should success add new item request")
    public void shouldSuccessCreateNewItemRequest() {
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

    @Test
    @DisplayName("should get all ir by owner")
    public void getAllByOwner() {
        when(irRepositoryMock.findItemRequestsByOwner_Id(owner.getId()))
                .thenReturn(List.of(expectedSavedIR));

        final ItemRequest result = irService.getAllByOwner(owner.getId()).iterator().next();

        Assertions.assertEquals(owner.getId(), result.getOwner().getId());
    }

    @Test
    @DisplayName("should get all ir")
    public void getAll() {
        when(irRepositoryMock.findAllItemRequestsExcludeOwner(anyInt(), any()))
                .thenReturn(List.of(expectedSavedIR));

        final ItemRequest result = irService.getAll(568, 0, 10).iterator().next();

        Assertions.assertEquals(owner.getId(), result.getOwner().getId());
        Assertions.assertEquals(expectedSavedIR.getId(), result.getId());
    }

    @Test
    @DisplayName("should get ir by id")
    public void getById() {
        when(irUtilMock.requireFindById(expectedSavedIR.getId()))
                .thenReturn(expectedSavedIR);

        final ItemRequest result = irService.getById(465, expectedSavedIR.getId());

        Assertions.assertEquals(expectedSavedIR.getId(), result.getId());
    }
}
