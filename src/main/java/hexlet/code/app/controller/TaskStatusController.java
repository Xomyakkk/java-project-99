package hexlet.code.app.controller;

import hexlet.code.app.dto.CreateTaskStatusDto;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.dto.UpdateTaskStatusDto;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    public TaskStatusController(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public ResponseEntity<List<TaskStatusDto>> getAllTaskStatuses() {
        List<TaskStatusDto> taskStatuses = taskStatusService.getAllTaskStatuses();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(taskStatuses);
    }

    @GetMapping("/{id}")
    public TaskStatusDto getTaskStatusById(@PathVariable Long id) {
        return taskStatusService.getTaskStatusById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDto createTaskStatus(@Valid @RequestBody CreateTaskStatusDto dto) {
        return taskStatusService.createTaskStatus(dto);
    }

    @PutMapping("/{id}")
    public TaskStatusDto updateTaskStatus(@PathVariable Long id, @Valid @RequestBody UpdateTaskStatusDto dto) {
        return taskStatusService.updateTaskStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskStatus(@PathVariable Long id) {
        taskStatusService.deleteTaskStatus(id);
    }
}
