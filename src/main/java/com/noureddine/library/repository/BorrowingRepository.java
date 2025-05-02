package com.noureddine.library.repository;

import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    Optional<Borrowing> findByBookCopy(BookCopy bookCopy);
    List<Borrowing> findByMember(User member);
    List<Borrowing> findByMemberAndStatus(User member, String status);
    List<Borrowing> findByMemberAndStatusIn(User member, List<String> statuses);
    boolean existsByBookCopy(BookCopy inventoryNumber);
    void deleteByBookCopy(BookCopy bookCopy);
    boolean existsByBookCopyAndMember(BookCopy bookCopy, User member);
    long countByStatus(String status);
}
