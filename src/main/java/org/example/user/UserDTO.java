package org.example.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String email;
    private String password; // Should be hashed before saving
    private String name;
    private String phoneNumber; // Optional, can be null
    private List<String> role; // e.g., "admin", "user"
    private boolean isActive; // true if the user is active, false if inactive
}
