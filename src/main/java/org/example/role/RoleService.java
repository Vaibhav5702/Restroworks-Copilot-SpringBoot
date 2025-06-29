package org.example.role;

public interface RoleService {
    Role findRoleByName(String name);
    Role saveRole(Role role);
}
