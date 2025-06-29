package org.example.user;

import org.apache.http.HttpStatus;
import org.example.authentication.TokenProvider;
import org.example.role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> generateToken(@RequestBody LoginUser loginUser){
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getEmail(), loginUser.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.status(HttpStatus.SC_OK).body(token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            User newUser = userService.saveUser(userDTO);
            return ResponseEntity.status(HttpStatus.SC_CREATED).body(newUser);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/addRole/{id}")
    @PreAuthorize("hasAnyRole('SUPER ADMIN', 'ADMIN')")
    public ResponseEntity<?> addRoleToUser(@PathVariable("id") String userId, @RequestBody Role role) {
        try{
            userService.addRoleToUser(userId,role.getName());
            return ResponseEntity.status(HttpStatus.SC_OK).body("Role added successfully");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Error adding role: " + e.getMessage());
        }
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String userId) {
        try {
            User user = userService.findUserById(userId);
            return ResponseEntity.status(HttpStatus.SC_OK).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("User not found: " + e.getMessage());
        }
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAnyRole('SUPER ADMIN', 'ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.status(HttpStatus.SC_OK).body(userService.findAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error retrieving users: " + e.getMessage());
        }
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") String userId, @RequestBody UserDTO userDTO) {
        try {
            User updatedUser = userService.updateUser(userId, userDTO);
            return ResponseEntity.status(HttpStatus.SC_OK).body(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("Error updating user: " + e.getMessage());
        }
    }
}
