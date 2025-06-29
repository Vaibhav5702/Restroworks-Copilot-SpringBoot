package org.example.role;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    // Custom query methods can be defined here if needed
    // For example, to find a role by its name:
    Optional<Role> findByName(String name);
}
