package org.example.backend.repositories;

import org.example.backend.entities.User;

import java.util.List;

public interface UserRepository {
    User findByEmail(String email);
    List<User> findAll();
    User findById(Long id);
    User create(User user);
    User update(Long id, User user);
    void changeActiveStatus(Long id, boolean active);

}
