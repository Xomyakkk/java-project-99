package hexlet.code.app.service;

import hexlet.code.app.dto.CreateTaskDto;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.dto.UpdateTaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       TaskStatusRepository taskStatusRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return toDto(task);
    }

    @Transactional
    public TaskDto createTask(CreateTaskDto dto) {
        Task task = new Task();
        task.setName(dto.getTitle());
        task.setDescription(dto.getContent());
        task.setIndex(dto.getIndex());

        TaskStatus taskStatus = taskStatusRepository.findBySlug(dto.getStatus())
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
        task.setTaskStatus(taskStatus);

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignee(assignee);
        }

        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    @Transactional
    public TaskDto updateTask(Long id, UpdateTaskDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (dto.getTitle() != null) {
            task.setName(dto.getTitle());
        }
        if (dto.getContent() != null) {
            task.setDescription(dto.getContent());
        }
        if (dto.getIndex() != null) {
            task.setIndex(dto.getIndex());
        }
        if (dto.getStatus() != null) {
            TaskStatus taskStatus = taskStatusRepository.findBySlug(dto.getStatus())
                    .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
            task.setTaskStatus(taskStatus);
        }
        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            task.setAssignee(assignee);
        }

        Task updated = taskRepository.save(task);
        return toDto(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private TaskDto toDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getIndex(),
                task.getCreatedAt(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getName(),
                task.getDescription(),
                task.getTaskStatus().getSlug()
        );
    }
}
