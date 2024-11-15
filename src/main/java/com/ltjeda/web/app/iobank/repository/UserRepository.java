package com.ltjeda.web.app.iobank.repository;

import com.ltjeda.web.app.iobank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsernameIgnoreCase(String username);
}
