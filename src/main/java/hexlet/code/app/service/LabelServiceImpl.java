package hexlet.code.app.service;

import hexlet.code.app.dto.CreateLabelDto;
import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.dto.UpdateLabelDto;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public LabelServiceImpl(LabelRepository labelRepository, LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    @Override
    public List<LabelDto> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LabelDto getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found"));
        return labelMapper.toDto(label);
    }

    @Override
    public LabelDto getLabelByName(String name) {
        Label label = labelRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Label not found"));
        return labelMapper.toDto(label);
    }

    @Override
    @Transactional
    public LabelDto createLabel(CreateLabelDto dto) {
        Label label = labelMapper.toEntity(dto);
        Label saved = labelRepository.save(label);
        return labelMapper.toDto(saved);
    }

    @Override
    @Transactional
    public LabelDto updateLabel(Long id, UpdateLabelDto dto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        labelMapper.update(dto, label);
        Label updated = labelRepository.save(label);
        return labelMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteLabel(Long id) {
        labelRepository.deleteById(id);
    }
}
