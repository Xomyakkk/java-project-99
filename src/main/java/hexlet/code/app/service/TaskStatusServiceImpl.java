package hexlet.code.app.service;

import hexlet.code.app.dto.CreateTaskStatusDto;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.dto.UpdateTaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;

    public TaskStatusServiceImpl(TaskStatusRepository taskStatusRepository, TaskRepository taskRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<TaskStatusDto> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskStatusDto getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
        return toDto(taskStatus);
    }

    @Override
    public TaskStatusDto getTaskStatusBySlug(String slug) {
        TaskStatus taskStatus = taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
        return toDto(taskStatus);
    }

    @Override
    @Transactional
    public TaskStatusDto createTaskStatus(CreateTaskStatusDto dto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(dto.getName());
        taskStatus.setSlug(dto.getSlug());
        TaskStatus saved = taskStatusRepository.save(taskStatus);
        return toDto(saved);
    }

    @Override
    @Transactional
    public TaskStatusDto updateTaskStatus(Long id, UpdateTaskStatusDto dto) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));

        if (dto.getName() != null) {
            taskStatus.setName(dto.getName());
        }
        if (dto.getSlug() != null) {
            taskStatus.setSlug(dto.getSlug());
        }

        TaskStatus updated = taskStatusRepository.save(taskStatus);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deleteTaskStatus(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));

        if (taskRepository.existsByTaskStatus(taskStatus)) {
            throw new hexlet.code.app.controller.TaskStatusController.ForbiddenException(
                    "Cannot delete status with associated tasks");
        }

        taskStatusRepository.deleteById(id);
    }

    private TaskStatusDto toDto(TaskStatus taskStatus) {
        return new TaskStatusDto(
                taskStatus.getId(),
                taskStatus.getName(),
                taskStatus.getSlug(),
                taskStatus.getCreatedAt()
        );
    }
}
