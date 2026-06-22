package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import org.springframework.data.jpa.domain.Specification;

public final class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> hasAssigneeId(Long assigneeId) {
        return (root, query, cb) -> {
            if (assigneeId == null) {
                return null;
            }
            return cb.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    public static Specification<Task> hasStatusSlug(String statusSlug) {
        return (root, query, cb) -> {
            if (statusSlug == null || statusSlug.isBlank()) {
                return null;
            }
            return cb.equal(root.get("taskStatus").get("slug"), statusSlug);
        };
    }

    public static Specification<Task> hasLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return null;
            }
            return cb.equal(root.join("labels").get("id"), labelId);
        };
    }

    public static Specification<Task> titleContains(String titleCont) {
        return (root, query, cb) -> {
            if (titleCont == null || titleCont.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + titleCont.toLowerCase() + "%");
        };
    }
}
