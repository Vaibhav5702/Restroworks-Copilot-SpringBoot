package org.example.user;

import java.util.List;

public interface UserService {
    User saveUser(UserDTO user);
    User findUserById(String id);
    List<User> findAllUsers();
    User updateUser(String id, UserDTO user);
    User addRoleToUser(String userId, String roleName);
}
