package com.arpit.emailverification.service;

import com.arpit.emailverification.model.User;
import com.arpit.emailverification.model.VerificationToken;
import com.arpit.emailverification.repo.UserRepo;
import com.arpit.emailverification.repo.VerificationTokenRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepo verificationTokenRepo;
    private final EmailService emailService;

    @Autowired
    UserService(UserRepo userRepo,
                BCryptPasswordEncoder passwordEncoder,
                AuthenticationManager authenticationManager,
                VerificationTokenRepo verificationTokenRepo,
                EmailService emailService
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.verificationTokenRepo = verificationTokenRepo;
        this.emailService = emailService;
    }


    public Boolean verify(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        return authentication.isAuthenticated();
    }

    public User register(User userDetails) {
        if (userRepo.findByUsername(userDetails.getUsername())!=null) {
            throw new UsernameNotFoundException("Username already exists");
        }
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        User user = userRepo.save(userDetails);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepo.save(verificationToken);

        String url = "http://localhost:8080/verify/" + token;
        emailService.sendEmail(user.getEmail(),
                "Verify Email",
                "Click to verification your email: "+url
        );
        return userRepo.save(user);
    }

    public boolean verifyToken(String token) {
        VerificationToken verificationToken = verificationTokenRepo.findByToken(token);
        if (verificationToken==null) {
            return false;
        }
        if(verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepo.save(user);

        return true;
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }
}
