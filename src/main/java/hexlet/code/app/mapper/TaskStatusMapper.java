package hexlet.code.app.mapper;

import hexlet.code.app.dto.CreateTaskStatusDto;
import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.dto.UpdateTaskStatusDto;
import hexlet.code.app.model.TaskStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskStatusMapper {

    TaskStatusDto toDto(TaskStatus taskStatus);

    TaskStatus toEntity(CreateTaskStatusDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateTaskStatusDto dto, @MappingTarget TaskStatus taskStatus);
}
