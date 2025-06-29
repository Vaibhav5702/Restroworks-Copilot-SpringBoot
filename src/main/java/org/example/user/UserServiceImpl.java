package org.example.user;

import org.example.config.PasswordEncoder;
import org.example.role.Role;
import org.example.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService,UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @Override
    public User saveUser(UserDTO user) {
        try {
            User newUser = new User();
            newUser.setEmail(user.getEmail());
            newUser.setName(user.getName());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            List<Role> roles = new ArrayList<>();
            roles.add(roleService.findRoleByName("USER"));
            newUser.setRole(roles);
            newUser.setActive(true);
            // Set other fields as necessary

            return userRepository.save(newUser);
        }
        catch (Exception e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }

    @Override
    public User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(String id, UserDTO user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        System.out.println(user.isActive());
        existingUser.setEmail(user.getEmail());
        existingUser.setName(user.getName());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setActive(user.isActive());
        // Update other fields as necessary
        return userRepository.save(existingUser);
    }

    @Override
    public User addRoleToUser(String userId, String roleName) {
        Role role;
        try {
            role = roleService.findRoleByName(roleName);
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Role not found: " + roleName, e);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        if(user.getRole().stream().anyMatch(r -> r.getName().equals(roleName))) {
            throw new RuntimeException("User already has role: " + roleName);
        }
        user.getRole().add(role);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(getAuthority(user).stream()
                        .map(SimpleGrantedAuthority::getAuthority)
                        .toArray(String[]::new))
                .build();
    }

    private List<SimpleGrantedAuthority> getAuthority(User user) {
        return user.getRole().stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
