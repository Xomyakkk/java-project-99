package hexlet.code.app.service;

import hexlet.code.app.dto.CreateTaskStatusDto;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.dto.UpdateTaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    public List<TaskStatusDto> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TaskStatusDto getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
        return toDto(taskStatus);
    }

    public TaskStatusDto getTaskStatusBySlug(String slug) {
        TaskStatus taskStatus = taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
        return toDto(taskStatus);
    }

    @Transactional
    public TaskStatusDto createTaskStatus(CreateTaskStatusDto dto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(dto.getName());
        taskStatus.setSlug(dto.getSlug());
        TaskStatus saved = taskStatusRepository.save(taskStatus);
        return toDto(saved);
    }

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

    @Transactional
    public void deleteTaskStatus(Long id) {
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
