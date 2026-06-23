package hexlet.code.app.service;

import hexlet.code.app.dto.CreateTaskStatusDto;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.dto.UpdateTaskStatusDto;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public TaskStatusServiceImpl(TaskStatusRepository taskStatusRepository, TaskStatusMapper taskStatusMapper) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskStatusMapper = taskStatusMapper;
    }

    @Override
    public List<TaskStatusDto> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TaskStatusDto getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
        return taskStatusMapper.toDto(taskStatus);
    }

    @Override
    public TaskStatusDto getTaskStatusBySlug(String slug) {
        TaskStatus taskStatus = taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));
        return taskStatusMapper.toDto(taskStatus);
    }

    @Override
    @Transactional
    public TaskStatusDto createTaskStatus(CreateTaskStatusDto dto) {
        TaskStatus taskStatus = taskStatusMapper.toEntity(dto);
        TaskStatus saved = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toDto(saved);
    }

    @Override
    @Transactional
    public TaskStatusDto updateTaskStatus(Long id, UpdateTaskStatusDto dto) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found"));

        taskStatusMapper.update(dto, taskStatus);
        TaskStatus updated = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteTaskStatus(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
