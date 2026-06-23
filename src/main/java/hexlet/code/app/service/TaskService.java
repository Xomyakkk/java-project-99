package hexlet.code.app.service;

import hexlet.code.app.dto.CreateTaskDto;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.dto.UpdateTaskDto;

import java.util.List;

public interface TaskService {

    List<TaskDto> getAllTasks();

    List<TaskDto> getTasksFiltered(String titleCont, Long assigneeId, String status, Long labelId);

    TaskDto getTaskById(Long id);

    TaskDto createTask(CreateTaskDto dto);

    TaskDto updateTask(Long id, UpdateTaskDto dto);

    void deleteTask(Long id);
}
