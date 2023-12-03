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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private final ItemResponseExtendedDto expectedResponseExtended = ItemObjectMapper
            .toItemResponseExtendedDto(expectedItem);

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

    @Test
    @DisplayName("should success map and create item comment")
    public void addComment() throws Exception {
        final CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("comment text")
                .build();
        final int authorId = ownerId + 1;
        final Comment expectedComment = Comment.builder()
                .id(1)
                .text(commentRequestDto.getText())
                .item(expectedItem)
                .author(expectedItem.getOwner().toBuilder().id(authorId).build())
                .build();
        final CommentResponseDto expectedCommentResponseDto = CommentObjectMapper.toResponseDto(expectedComment);

        when(itemServiceMock.addComment(commentRequestDto, itemId, authorId))
                .thenReturn(expectedComment);

        mvc.perform(post("/items/" + expectedItem.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .header("X-Sharer-User-Id", authorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedCommentResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(expectedCommentResponseDto.getText())))
                .andExpect(jsonPath("$.authorName", is(expectedCommentResponseDto.getAuthorName())));
    }

    @Test
    @DisplayName("should success map and get item by id")
    public void getItemById() throws Exception {
        when(itemServiceMock.getById(itemId, ownerId))
                .thenReturn(expectedResponseExtended);

        mvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponseExtended.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(expectedResponseExtended.getName())))
                .andExpect(jsonPath("$.description", is(expectedResponseExtended.getDescription())));
    }

    @Test
    @DisplayName("should success map and get all owner items")
    public void getAllOwnerItems() throws Exception {
        when(itemServiceMock.getAllUserItems(ownerId))
                .thenReturn(List.of(expectedResponseExtended));

        mvc.perform(get("/items/")
                        .header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(expectedResponseExtended.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(expectedResponseExtended.getName())))
                .andExpect(jsonPath("$.[0].description", is(expectedResponseExtended.getDescription())));
    }

    @Test
    @DisplayName("should success map and search items by text query")
    public void searchItemsByQuery() throws Exception {
        when(itemServiceMock.search("text search"))
                .thenReturn(List.of(expectedItem));

        mvc.perform(get("/items/search")
                        .param("text", "text search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(expectedResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(expectedResponse.getName())))
                .andExpect(jsonPath("$.[0].description", is(expectedResponse.getDescription())));
    }
}
