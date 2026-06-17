package hexlet.code.app.controller;

import hexlet.code.app.dto.CreateLabelDto;
import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.dto.UpdateLabelDto;
import hexlet.code.app.service.LabelService;
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
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    public ResponseEntity<List<LabelDto>> getAllLabels() {
        List<LabelDto> labels = labelService.getAllLabels();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    @GetMapping("/{id}")
    public LabelDto getLabelById(@PathVariable Long id) {
        return labelService.getLabelById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDto createLabel(@Valid @RequestBody CreateLabelDto dto) {
        return labelService.createLabel(dto);
    }

    @PutMapping("/{id}")
    public LabelDto updateLabel(@PathVariable Long id, @Valid @RequestBody UpdateLabelDto dto) {
        return labelService.updateLabel(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }

    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(message);
        }
    }
}
