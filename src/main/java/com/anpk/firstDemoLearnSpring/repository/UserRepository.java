package com.anpk.firstDemoLearnSpring.repository;

import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Tương đương với db.Users.FirstOrDefault(x => x.Username == username)
    Optional<User> findByUsername(String username);

    // Tương đương với db.Users.Any(x => x.Email == email)
    boolean existsByEmail(String email);

    // Tương đương với db.Users.Any(x => x.Username == username)
    boolean existsByUsername(String username);
}

