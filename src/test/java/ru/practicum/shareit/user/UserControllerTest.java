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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    @DisplayName("should success map and update user")
    public void updateUser() throws Exception {
        final UserRequestDto userRequestDto = new UserRequestDto("updated username", null);
        final User expectedUpdatedUser = User.builder().id(1).name(userRequestDto.getName()).email("old email").build();
        final UserResponseDto expectedUserResponseDto = new UserResponseDto(expectedUpdatedUser.getId(),
                expectedUpdatedUser.getName(), expectedUpdatedUser.getEmail());

        when(userServiceMock.update(anyInt(), any()))
                .thenReturn(expectedUpdatedUser);

        mvc.perform(patch("/users/" + expectedUserResponseDto.getId())
                        .content(mapper.writeValueAsString(userRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUserResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(expectedUserResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedUserResponseDto.getEmail())));
    }

    @Test
    @DisplayName("should success map and delete user by id")
    public void deleteUser() throws Exception {
        final User expectedDeletedUser = User.builder().id(1).name("deleted user").email("user email").build();
        final UserResponseDto expectedUserResponseDto = new UserResponseDto(expectedDeletedUser.getId(),
                expectedDeletedUser.getName(), expectedDeletedUser.getEmail());

        when(userServiceMock.deleteById(expectedDeletedUser.getId()))
                .thenReturn(expectedDeletedUser);

        mvc.perform(delete("/users/" + expectedDeletedUser.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUserResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(expectedUserResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedUserResponseDto.getEmail())));
    }

    @Test
    @DisplayName("should success map and get user by id")
    public void getUserById() throws Exception {
        final User expectedUser = User.builder().id(1).name("user").email("user email").build();
        final UserResponseDto expectedUserResponseDto = new UserResponseDto(expectedUser.getId(),
                expectedUser.getName(), expectedUser.getEmail());

        when(userServiceMock.getById(expectedUser.getId()))
                .thenReturn(expectedUser);


        mvc.perform(get("/users/" + expectedUser.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUserResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(expectedUserResponseDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedUserResponseDto.getEmail())));
    }

    @Test
    @DisplayName("should success map and get all users")
    public void getAllUsers() throws Exception {
        final User expectedUser = User.builder().id(1).name("user").email("user email").build();
        final UserResponseDto expectedUserResponseDto = new UserResponseDto(expectedUser.getId(),
                expectedUser.getName(), expectedUser.getEmail());

        when(userServiceMock.getAll())
                .thenReturn(List.of(expectedUser));

        mvc.perform(get("/users/")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(expectedUserResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(expectedUserResponseDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(expectedUserResponseDto.getEmail())));
    }
}