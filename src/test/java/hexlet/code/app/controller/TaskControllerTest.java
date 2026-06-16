package hexlet.code.app.controller;

import hexlet.code.app.dto.CreateTaskDto;
import hexlet.code.app.dto.LoginRequestDto;
import hexlet.code.app.dto.UpdateTaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
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
import org.springframework.transaction.annotation.Transactional;
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
@Transactional
class TaskControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TaskRepository taskRepository;

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
        taskRepository.deleteAll();
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

    private TaskStatus createTestTaskStatus(String name, String slug) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        return taskStatusRepository.save(taskStatus);
    }

    @Test
    void testGetAllTasks() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetTaskById() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        Task task = createTestTask("Test Task", "Test description");

        mockMvc.perform(get("/api/tasks/{id}", task.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.content").value("Test description"));
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        mockMvc.perform(get("/api/tasks/{id}", 999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTask() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        TaskStatus taskStatus = createTestTaskStatus("Draft", "draft");

        CreateTaskDto dto = new CreateTaskDto();
        dto.setTitle("Test title");
        dto.setContent("Test content");
        dto.setIndex(12);
        dto.setStatus("draft");

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test title"))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.index").value(12))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void testCreateTaskWithAssignee() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        User assignee = createTestUser("assignee@example.com");
        TaskStatus taskStatus = createTestTaskStatus("Draft", "draft");

        CreateTaskDto dto = new CreateTaskDto();
        dto.setTitle("Task with assignee");
        dto.setContent("Task content");
        dto.setAssigneeId(assignee.getId());
        dto.setStatus("draft");

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assignee_id").value(assignee.getId()))
                .andExpect(jsonPath("$.title").value("Task with assignee"));
    }

    @Test
    void testCreateTaskWithInvalidData() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        createTestTaskStatus("Draft", "draft");

        CreateTaskDto dto = new CreateTaskDto();
        dto.setTitle("");
        dto.setStatus("draft");

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTask() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        Task task = createTestTask("Old title", "Old content");

        UpdateTaskDto dto = new UpdateTaskDto();
        dto.setTitle("New title");
        dto.setContent("New content");

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.content").value("New content"));
    }

    @Test
    void testPartialUpdateTask() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        Task task = createTestTask("Original title", "Original content");

        UpdateTaskDto dto = new UpdateTaskDto();
        dto.setTitle("Updated title");

        mockMvc.perform(put("/api/tasks/{id}", task.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Updated title"))
                .andExpect(jsonPath("$.content").value("Original content"));
    }

    @Test
    void testDeleteTask() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        Task task = createTestTask("To delete", "Will be deleted");

        mockMvc.perform(delete("/api/tasks/{id}", task.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertNull(taskRepository.findById(task.getId()).orElse(null));
    }

    @Test
    void testGetAllTasksReturnsList() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        createTestTask("Task 1", "Content 1");
        createTestTask("Task 2", "Content 2");

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private Task createTestTask(String title, String description) {
        TaskStatus taskStatus = createTestTaskStatus("ToReview", "to_review");
        Task task = new Task();
        task.setName(title);
        task.setDescription(description);
        task.setTaskStatus(taskStatus);
        return taskRepository.save(task);
    }
}
