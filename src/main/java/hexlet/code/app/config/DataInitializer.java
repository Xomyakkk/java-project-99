package hexlet.code.app.config;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                    TaskStatusRepository taskStatusRepository,
                                    LabelRepository labelRepository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setEmail("hexlet@example.com");
                admin.setPassword(passwordEncoder.encode("qwerty"));
                userRepository.save(admin);
            }

            if (taskStatusRepository.count() == 0) {
                String[][] defaultStatuses = {
                    {"Draft", "draft"},
                    {"ToReview", "to_review"},
                    {"ToBeFixed", "to_be_fixed"},
                    {"ToPublish", "to_publish"},
                    {"Published", "published"}
                };

                for (String[] status : defaultStatuses) {
                    TaskStatus taskStatus = new TaskStatus();
                    taskStatus.setName(status[0]);
                    taskStatus.setSlug(status[1]);
                    taskStatusRepository.save(taskStatus);
                }
            }

            if (labelRepository.count() == 0) {
                String[] defaultLabels = {"feature", "bug"};

                for (String labelName : defaultLabels) {
                    Label label = new Label();
                    label.setName(labelName);
                    labelRepository.save(label);
                }
            }
        };
    }
}
