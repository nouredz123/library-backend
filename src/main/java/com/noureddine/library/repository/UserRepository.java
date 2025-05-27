package com.noureddine.library.repository;

import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    Long countByAccountStatus(String status);
    Long countByLastActiveDateAfterAndAccountStatusAndRole(LocalDate date, String status, String role);
    List<User> findByRoleAndAccountStatus(String roleMember, String upperCase);
    Boolean existsByEmail(String email);
    @Query("SELECT u FROM User u " +
            "WHERE REPLACE(LOWER(u.fullName), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(u.identifier), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(u.email), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) ")
    List<User> searchUsers(@Param("keyword") String keyword);
    @Query("SELECT u FROM User u " +
            "WHERE ( " +
            "REPLACE(LOWER(u.fullName), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(u.identifier), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(u.email), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) "+
            ") " +
            "AND (:role IS NULL OR u.role = :role)")
    List<User> searchUsersAndRole(@Param("keyword") String keyword, @Param("role") String role);
    @Query("SELECT u FROM User u " +
            "WHERE (" +
            "  REPLACE(LOWER(u.fullName), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR REPLACE(LOWER(u.identifier), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR REPLACE(LOWER(u.email), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            ") " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:accountStatus IS NULL OR u.accountStatus = :accountStatus)")
    List<User> searchUsersAndRoleAndAccountStatus(@Param("keyword") String keyword, @Param("role") String role, @Param("accountStatus") String accountStatus);
}
