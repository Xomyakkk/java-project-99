package hexlet.code.app.service;

import hexlet.code.app.dto.CreateTaskDto;
import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.dto.UpdateTaskDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskSpecification;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskStatusRepository taskStatusRepository,
                           UserRepository userRepository,
                           LabelRepository labelRepository) {
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
    }

    @Override
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> getTasksFiltered(String titleCont, Long assigneeId, String status, Long labelId) {
        Specification<Task> spec = Specification.allOf();

        if (titleCont != null && !titleCont.isBlank()) {
            spec = spec.and(TaskSpecification.titleContains(titleCont));
        }
        if (assigneeId != null) {
            spec = spec.and(TaskSpecification.hasAssigneeId(assigneeId));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and(TaskSpecification.hasStatusSlug(status));
        }
        if (labelId != null) {
            spec = spec.and(TaskSpecification.hasLabelId(labelId));
        }

        return taskRepository.findAll(spec).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return toDto(task);
    }

    @Override
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

        if (dto.getLabelIds() != null && !dto.getLabelIds().isEmpty()) {
            Set<Label> labels = new HashSet<>();
            for (Long labelId : dto.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new RuntimeException("Label not found"));
                labels.add(label);
            }
            task.setLabels(labels);
        }

        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    @Override
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
        if (dto.getLabelIds() != null) {
            Set<Label> labels = new HashSet<>();
            for (Long labelId : dto.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new RuntimeException("Label not found"));
                labels.add(label);
            }
            task.setLabels(labels);
        }

        Task updated = taskRepository.save(task);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private TaskDto toDto(Task task) {
        List<LabelDto> labelDtos = task.getLabels().stream()
                .map(label -> new LabelDto(label.getId(), label.getName(), label.getCreatedAt()))
                .collect(Collectors.toList());
        List<Long> taskLabelIds = task.getLabels().stream()
                .map(Label::getId)
                .collect(Collectors.toList());

        return new TaskDto(
                task.getId(),
                task.getIndex(),
                task.getCreatedAt(),
                taskLabelIds,
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getName(),
                task.getDescription(),
                task.getTaskStatus().getSlug(),
                labelDtos
        );
    }
}
