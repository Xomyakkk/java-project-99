package hexlet.code.app.controller;

import hexlet.code.app.dto.CreateUserDto;
import hexlet.code.app.dto.UpdateUserDto;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
    }

    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = createTestUser("test@example.com", "Test", "User");

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("new@example.com");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testCreateUserWithInvalidEmail() throws Exception {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("invalid-email");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWithShortPassword() throws Exception {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("test@example.com");
        dto.setPassword("ab");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = createTestUser("old@example.com", "Old", "Name");

        UpdateUserDto dto = new UpdateUserDto();
        dto.setEmail("new@example.com");
        dto.setPassword("newpassword");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.firstName").value("Old"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void testPartialUpdateUser() throws Exception {
        User user = createTestUser("test@example.com", "First", "Last");

        UpdateUserDto dto = new UpdateUserDto();
        dto.setFirstName("Updated");

        mockMvc.perform(put("/api/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.lastName").value("Last"));
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = createTestUser("delete@example.com", "Delete", "User");

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertNull(userRepository.findById(user.getId()).orElse(null));
    }

    @Test
    void testPasswordIsHashed() throws Exception {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("hash@example.com");
        dto.setPassword("plainpassword");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        User savedUser = userRepository.findByEmail("hash@example.com").orElseThrow();
        assertNotNull(savedUser.getPassword());
        assertTrue(savedUser.getPassword().length() > 0);
        assertTrue(savedUser.getPassword() != "plainpassword");
    }

    @Test
    void testGetAllUsersReturnsList() throws Exception {
        createTestUser("user1@example.com", "User", "One");
        createTestUser("user2@example.com", "User", "Two");

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].password").doesNotExist());
    }

    private User createTestUser(String email, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword("password123");
        return userRepository.save(user);
    }
}
