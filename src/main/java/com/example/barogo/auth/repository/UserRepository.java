package com.example.barogo.auth.repository;

import com.example.barogo.auth.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByLoginId(String id);
  boolean existsByLoginId(String id);

}
