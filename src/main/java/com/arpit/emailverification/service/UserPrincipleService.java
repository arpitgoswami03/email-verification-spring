package com.arpit.emailverification.service;

import com.arpit.emailverification.model.User;
import com.arpit.emailverification.model.UserPrinciple;
import com.arpit.emailverification.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipleService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserPrincipleService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserPrinciple loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        return new UserPrinciple(user);
    }
}
