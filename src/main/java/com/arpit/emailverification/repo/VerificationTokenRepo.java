package com.arpit.emailverification.repo;

import com.arpit.emailverification.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepo extends JpaRepository<VerificationToken,String> {
    VerificationToken findByToken(String token);
}
