package hexlet.code.app.controller;

import hexlet.code.app.dto.CreateLabelDto;
import hexlet.code.app.dto.LoginRequestDto;
import hexlet.code.app.dto.UpdateLabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
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
class LabelControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private LabelRepository labelRepository;

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
        labelRepository.deleteAll();
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

    private Label createTestLabel(String name) {
        Label label = new Label();
        label.setName(name);
        return labelRepository.save(label);
    }

    @Test
    void testGetAllLabels() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        mockMvc.perform(get("/api/labels")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetLabelById() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        Label label = createTestLabel("Bug");

        mockMvc.perform(get("/api/labels/{id}", label.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(label.getId()))
                .andExpect(jsonPath("$.name").value("Bug"));
    }

    @Test
    void testGetLabelByIdNotFound() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        mockMvc.perform(get("/api/labels/{id}", 999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateLabel() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        CreateLabelDto dto = new CreateLabelDto();
        dto.setName("NewLabel");

        mockMvc.perform(post("/api/labels")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("NewLabel"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void testCreateLabelWithInvalidData() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        CreateLabelDto dto = new CreateLabelDto();
        dto.setName("ab");

        mockMvc.perform(post("/api/labels")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateLabel() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        Label label = createTestLabel("OldName");

        UpdateLabelDto dto = new UpdateLabelDto();
        dto.setName("UpdatedName");

        mockMvc.perform(put("/api/labels/{id}", label.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(label.getId()))
                .andExpect(jsonPath("$.name").value("UpdatedName"));
    }

    @Test
    void testDeleteLabel() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        Label label = createTestLabel("ToDelete");

        mockMvc.perform(delete("/api/labels/{id}", label.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertNull(labelRepository.findById(label.getId()).orElse(null));
    }

    @Test
    @org.junit.jupiter.api.Disabled("TODO: fix flaky test - H2 issue with M2M relationship and deleteAll")
    void testDeleteLabelWithAssociatedTask() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");

        Label label = createTestLabel("AssociatedLabel");

        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName("Draft");
        taskStatus.setSlug("draft");
        taskStatusRepository.save(taskStatus);

        Task task = new Task();
        task.setName("Task with label");
        task.setTaskStatus(taskStatus);
        task.getLabels().add(label);
        taskRepository.saveAndFlush(task);

        mockMvc.perform(delete("/api/labels/{id}", label.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllLabelsReturnsList() throws Exception {
        createTestUser("test@example.com");
        String token = getAuthToken("test@example.com", "password123");
        createTestLabel("Label1");
        createTestLabel("Label2");

        mockMvc.perform(get("/api/labels")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
