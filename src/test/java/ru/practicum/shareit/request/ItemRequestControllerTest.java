package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.IRObjectMapper;
import ru.practicum.shareit.request.dto.IRRequestDto;
import ru.practicum.shareit.request.dto.IRResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    @MockBean
    private final ItemRequestService irServiceMock;

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final int ownerId = 1;
    private final int irId = 1;
    private final ItemRequest expectedIR = ItemRequest.builder()
            .id(irId)
            .description("ir desc")
            .owner(User.builder().id(ownerId).name("ir owner").email("owner email").build())
            .created(LocalDateTime.now().minusDays(1))
            .build();
    private final IRResponseDto expectedResponse = IRObjectMapper.toResponseDto(expectedIR);

    @Test
    @DisplayName("should success map and create new item request")
    public void createNewItemRequest() throws Exception {
        final IRRequestDto requestDto = IRRequestDto.builder().description(expectedIR.getDescription()).build();
        when(irServiceMock.createNew(requestDto, ownerId))
                .thenReturn(expectedIR);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(expectedResponse.getDescription())));
    }

    @Test
    @DisplayName("should success map and get all owner requests")
    public void getAllOwnerItemRequests() throws Exception {
        when(irServiceMock.getAllByOwner(ownerId))
                .thenReturn(List.of(expectedIR));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(expectedResponse.getDescription())));
    }

    @Test
    @DisplayName("should success map and get all other users requests")
    public void getAllOtherUsersItemRequests() throws Exception {
        final int otherOwnerId = ownerId + 1;
        when(irServiceMock.getAll(ownerId, 0, 10))
                .thenReturn(List.of(expectedIR.toBuilder()
                        .owner(expectedIR.getOwner().toBuilder().id(otherOwnerId).build())
                        .build()));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(expectedResponse.getDescription())));
    }

    @Test
    @DisplayName("should success map and request by id")
    public void getRequestById() throws Exception {
        when(irServiceMock.getById(ownerId, irId))
                .thenReturn(expectedIR);

        mvc.perform(get("/requests/" + irId)
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(expectedResponse.getDescription())));
    }
}
