package hexlet.code.app.service;

import hexlet.code.app.dto.CreateUserDto;
import hexlet.code.app.dto.UpdateUserDto;
import hexlet.code.app.dto.UserDto;
import hexlet.code.app.exception.ForbiddenOperationException;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       TaskRepository taskRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toDto(user);
    }

    @Transactional
    public UserDto createUser(CreateUserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updated = userRepository.save(user);
        return toDto(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (taskRepository.existsByAssignee(user)) {
            throw new ForbiddenOperationException(
                    "Cannot delete user with assigned tasks");
        }

        userRepository.deleteById(id);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCreatedAt()
        );
    }
}
