package com.loakdv.sarafan.repo;

import com.loakdv.sarafan.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepo extends JpaRepository<User, String> {
}
