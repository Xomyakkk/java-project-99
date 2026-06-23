package hexlet.code.app.mapper;

import hexlet.code.app.dto.CreateLabelDto;
import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.dto.UpdateLabelDto;
import hexlet.code.app.model.Label;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    LabelDto toDto(Label label);

    Label toEntity(CreateLabelDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UpdateLabelDto dto, @MappingTarget Label label);
}
