package edu.icet.service;

import edu.icet.dto.UserDTO;
import edu.icet.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserByUsername(String username);

    User createUser(UserDTO userDTO);

    User updateUser(String username, UserDTO userDTO);

    boolean deleteUser(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}