package hexlet.code.app.service;

import hexlet.code.app.dto.CreateUserDto;
import hexlet.code.app.dto.UpdateUserDto;
import hexlet.code.app.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto createUser(CreateUserDto dto);

    UserDto updateUser(Long id, UpdateUserDto dto);

    void deleteUser(Long id);
}
