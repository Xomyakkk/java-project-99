package hexlet.code.app.controller;

import hexlet.code.app.dto.CreateTaskStatusDto;
import hexlet.code.app.dto.LoginRequestDto;
import hexlet.code.app.dto.UpdateTaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TaskStatusControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String getAuthToken(String email, String password) throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setUsername(email);
        loginDto.setPassword(password);

        return mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private User createTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        return userRepository.save(user);
    }

    @Test
    void testGetAllTaskStatuses() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        mockMvc.perform(get("/api/task_statuses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetTaskStatusById() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        TaskStatus taskStatus = createTestTaskStatus("TestStatus", "test_status");

        mockMvc.perform(get("/api/task_statuses/{id}", taskStatus.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskStatus.getId()))
                .andExpect(jsonPath("$.name").value("TestStatus"))
                .andExpect(jsonPath("$.slug").value("test_status"));
    }

    @Test
    void testGetTaskStatusByIdNotFound() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        mockMvc.perform(get("/api/task_statuses/{id}", 999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTaskStatus() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        CreateTaskStatusDto dto = new CreateTaskStatusDto();
        dto.setName("NewStatus");
        dto.setSlug("new_status");

        mockMvc.perform(post("/api/task_statuses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("NewStatus"))
                .andExpect(jsonPath("$.slug").value("new_status"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void testCreateTaskStatusWithInvalidData() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        CreateTaskStatusDto dto = new CreateTaskStatusDto();
        dto.setName("");
        dto.setSlug("");

        mockMvc.perform(post("/api/task_statuses")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        TaskStatus taskStatus = createTestTaskStatus("OldName", "old_slug");

        UpdateTaskStatusDto dto = new UpdateTaskStatusDto();
        dto.setName("UpdatedName");

        mockMvc.perform(put("/api/task_statuses/{id}", taskStatus.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskStatus.getId()))
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.slug").value("old_slug"));
    }

    @Test
    void testPartialUpdateTaskStatus() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        TaskStatus taskStatus = createTestTaskStatus("OriginalName", "original_slug");

        UpdateTaskStatusDto dto = new UpdateTaskStatusDto();
        dto.setSlug("new_slug");

        mockMvc.perform(put("/api/task_statuses/{id}", taskStatus.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskStatus.getId()))
                .andExpect(jsonPath("$.name").value("OriginalName"))
                .andExpect(jsonPath("$.slug").value("new_slug"));
    }

    @Test
    void testDeleteTaskStatus() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        TaskStatus taskStatus = createTestTaskStatus("ToDelete", "to_delete");

        mockMvc.perform(delete("/api/task_statuses/{id}", taskStatus.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertNull(taskStatusRepository.findById(taskStatus.getId()).orElse(null));
    }

    @Test
    void testGetAllTaskStatusesReturnsList() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        createTestTaskStatus("Status1", "status1");
        createTestTaskStatus("Status2", "status2");

        mockMvc.perform(get("/api/task_statuses")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private TaskStatus createTestTaskStatus(String name, String slug) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        return taskStatusRepository.save(taskStatus);
    }
}
