package org.example.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/createRole")
    @PreAuthorize("hasRole('SUPER ADMIN')")
    public ResponseEntity<String> createRole(@RequestBody Role role) {
        roleService.saveRole(role);
        // Logic to create a role
        return ResponseEntity.ok("Role created successfully");
    }
}
