package org.example.backend.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.backend.dto.CreateUserRequest;
import org.example.backend.dto.UpdateUserRequest;
import org.example.backend.entities.User;
import org.example.backend.repositories.UserRepository;
import org.example.backend.util.JwtUtil;
import javax.inject.Inject;
import java.util.List;

public class UserService {

    @Inject
    UserRepository userRepository;

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public boolean isAuthorized(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            JwtUtil.verifyToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<User> findAll(String currentRole) {
        requireAdmin(currentRole);
        return userRepository.findAll();
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Samo administrator ima dozvolu za ovu akciju.");
        }
    }

    public User create(CreateUserRequest request, String currentRole) {
        requireAdmin(currentRole);

        validateCreateUserRequest(request);

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Korisnik sa tim emailom već postoji.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Lozinke se ne poklapaju.");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());
        user.setActive(true);

        user.setPasswordHash(
                DigestUtils.sha256Hex(request.getPassword())
        );

        return userRepository.create(user);
    }

    private void validateCreateUserRequest(CreateUserRequest request) {

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email je obavezan.");
        }

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("Ime je obavezno.");
        }

        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Prezime je obavezno.");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Lozinka je obavezna.");
        }

        if (request.getConfirmPassword() == null || request.getConfirmPassword().trim().isEmpty()) {
            throw new RuntimeException("Potvrda lozinke je obavezna.");
        }

        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new RuntimeException("Tip korisnika je obavezan.");
        }
    }

    public User update(Long id, UpdateUserRequest request, String currentRole) {
        requireAdmin(currentRole);

        User existing = userRepository.findById(id);

        if (existing == null) {
            throw new RuntimeException("Korisnik ne postoji.");
        }

        validateUpdateUserRequest(request);

        User sameEmail = userRepository.findByEmail(request.getEmail());

        if (sameEmail != null && !sameEmail.getId().equals(id)) {
            throw new RuntimeException("Email je već zauzet.");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());

        return userRepository.update(id, user);
    }

    private void validateUpdateUserRequest(UpdateUserRequest request) {

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email je obavezan.");
        }

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("Ime je obavezno.");
        }

        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Prezime je obavezno.");
        }

        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new RuntimeException("Tip korisnika je obavezan.");
        }
    }

    public void changeActiveStatus(Long id, boolean active, String currentRole) {

        requireAdmin(currentRole);

        User user = userRepository.findById(id);

        if (user == null) {
            throw new RuntimeException("Korisnik ne postoji.");
        }

        if ("ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Administrator ne može biti deaktiviran.");
        }

        userRepository.changeActiveStatus(id, active);
    }
}
