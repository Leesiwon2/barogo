package com.example.barogo.auth.repository;

import com.example.barogo.auth.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findById(String id);
  boolean existsById(String id);

}
