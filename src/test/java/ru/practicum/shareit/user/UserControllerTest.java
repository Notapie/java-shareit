package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserObjectMapper;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    @MockBean
    private final UserService userServiceMock;

    private final ObjectMapper mapper;
    private final MockMvc mvc;

    @Test
    @DisplayName("should success map and dispatch user creation")
    public void saveNewUser() throws Exception {
        final UserRequestDto userRequestDto = new UserRequestDto("first user", "user email");
        final UserResponseDto expectedUserResponseDto = new UserResponseDto(1, userRequestDto.getName(),
                userRequestDto.getEmail());

        when(userServiceMock.create(any()))
                .thenAnswer(invocationOnMock -> {
                    final UserRequestDto request = invocationOnMock.getArgument(0, UserRequestDto.class);
                    return UserObjectMapper.fromUserRequestDto(request, expectedUserResponseDto.getId());
                });


        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUserResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(expectedUserResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedUserResponseDto.getEmail())));
    }
}
