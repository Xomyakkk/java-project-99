package hexlet.code.app.service;

import hexlet.code.app.dto.CreateLabelDto;
import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.dto.UpdateLabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final TaskRepository taskRepository;

    public LabelServiceImpl(LabelRepository labelRepository, TaskRepository taskRepository) {
        this.labelRepository = labelRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<LabelDto> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LabelDto getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found"));
        return toDto(label);
    }

    @Override
    public LabelDto getLabelByName(String name) {
        Label label = labelRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Label not found"));
        return toDto(label);
    }

    @Override
    @Transactional
    public LabelDto createLabel(CreateLabelDto dto) {
        Label label = new Label();
        label.setName(dto.getName());
        Label saved = labelRepository.save(label);
        return toDto(saved);
    }

    @Override
    @Transactional
    public LabelDto updateLabel(Long id, UpdateLabelDto dto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        if (dto.getName() != null) {
            label.setName(dto.getName());
        }

        Label updated = labelRepository.save(label);
        return toDto(updated);
    }

    @Override
    @Transactional
    public void deleteLabel(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        if (taskRepository.existsByLabelsContaining(label)) {
            throw new hexlet.code.app.controller.LabelController.ForbiddenException(
                    "Cannot delete label with associated tasks");
        }

        labelRepository.deleteById(id);
    }

    private LabelDto toDto(Label label) {
        return new LabelDto(
                label.getId(),
                label.getName(),
                label.getCreatedAt()
        );
    }
}
