package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemObjectMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    @MockBean
    private final ItemService itemServiceMock;

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    private final int ownerId = 1;
    private final int itemId = 1;

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .name("my item")
            .available(true)
            .description("my item desc")
            .build();

    private final Item expectedItem = ItemObjectMapper.fromItemRequestDto(requestDto).toBuilder()
            .id(itemId)
            .owner(User.builder()
                    .id(ownerId)
                    .name("owner")
                    .email("owner email")
                    .build())
            .build();

    private final ItemResponseDto expectedResponse = ItemObjectMapper.toItemResponseDto(expectedItem);

    @Test
    @DisplayName("should success map and create new item")
    public void createItem() throws Exception {
        when(itemServiceMock.create(requestDto, ownerId))
                .thenReturn(expectedItem);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(expectedResponse.getName())))
                .andExpect(jsonPath("$.description", is(expectedResponse.getDescription())));
    }

    @Test
    @DisplayName("should success map and update item")
    public void updateItem() throws Exception {
        final ItemRequestDto updateRequest = ItemRequestDto.builder()
                .name("updated name")
                .build();

        when(itemServiceMock.update(ownerId, itemId, updateRequest))
                .thenReturn(expectedItem.toBuilder().name(updateRequest.getName()).build());

        mvc.perform(patch("/items/" + expectedItem.getId())
                        .content(mapper.writeValueAsString(updateRequest))
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(updateRequest.getName())))
                .andExpect(jsonPath("$.description", is(expectedResponse.getDescription())));
    }
}
