package org.example.backend.services;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.LoginResponse;
import org.example.backend.entities.User;
import org.example.backend.repositories.UserRepository;
import org.example.backend.util.JwtUtil;

import javax.inject.Inject;

public class AuthService {

    @Inject
    UserRepository userRepository;


    public LoginResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email je obavezan.");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Lozinka je obavezna.");
        }

        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            throw new RuntimeException("Korisnik sa ovim emailom ne postoji.");
        }

        String passwordHash = DigestUtils.sha256Hex(request.getPassword());

        if (!passwordHash.equals(user.getPasswordHash())) {
            throw new RuntimeException("Pogrešna lozinka.");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Korisnik nije aktivan.");
        }

        String token = JwtUtil.generateToken(user);

        return new LoginResponse(token);
    }
}