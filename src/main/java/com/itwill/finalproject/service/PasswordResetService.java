package com.itwill.finalproject.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.itwill.finalproject.domain.PasswordGenerator;
import com.itwill.finalproject.domain.User;
import com.itwill.finalproject.repository.UserRepository;

import jakarta.mail.MessagingException;



@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public void resetPassword(String email) throws MessagingException {
        User user = userRepository.findByUserEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        String tempPassword = PasswordGenerator.generateRandomPassword(10);
        user.setUserPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, tempPassword);
    }
}