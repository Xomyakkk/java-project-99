package hexlet.code.app.service;

import hexlet.code.app.dto.CreateLabelDto;
import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.dto.UpdateLabelDto;

import java.util.List;

public interface LabelService {

    List<LabelDto> getAllLabels();

    LabelDto getLabelById(Long id);

    LabelDto getLabelByName(String name);

    LabelDto createLabel(CreateLabelDto dto);

    LabelDto updateLabel(Long id, UpdateLabelDto dto);

    void deleteLabel(Long id);
}
