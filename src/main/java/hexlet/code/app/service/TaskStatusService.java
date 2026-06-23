package hexlet.code.app.service;

import hexlet.code.app.dto.CreateTaskStatusDto;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.dto.UpdateTaskStatusDto;

import java.util.List;

public interface TaskStatusService {

    List<TaskStatusDto> getAllTaskStatuses();

    TaskStatusDto getTaskStatusById(Long id);

    TaskStatusDto getTaskStatusBySlug(String slug);

    TaskStatusDto createTaskStatus(CreateTaskStatusDto dto);

    TaskStatusDto updateTaskStatus(Long id, UpdateTaskStatusDto dto);

    void deleteTaskStatus(Long id);
}
