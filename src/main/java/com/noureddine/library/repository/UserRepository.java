package com.noureddine.library.repository;

import com.noureddine.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Page<User> findByRole(String role, Pageable pageable);
    Long countByAccountStatus(String status);
    Long countByLastActiveDateAfterAndAccountStatusAndRole(LocalDate date, String status, String role);
}
