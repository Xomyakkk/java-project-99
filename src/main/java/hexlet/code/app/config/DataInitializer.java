package hexlet.code.app.config;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setEmail("hexlet@example.com");
                admin.setPassword(passwordEncoder.encode("qwerty"));
                userRepository.save(admin);
            }
        };
    }
}
