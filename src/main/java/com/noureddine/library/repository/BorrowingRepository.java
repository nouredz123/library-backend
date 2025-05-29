package com.noureddine.library.repository;

import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    List<Borrowing> findByMember(User member);
    List<Borrowing> findByMemberAndStatus(User member, String status);
    long countByStatus(String status);
    List<Borrowing> findByStatus(String status);
    List<Borrowing> findByMemberAndStatusIn(User member, List<String> pickedUp);
    boolean existsByBookCopy_BookAndMemberAndStatusIn(Book book, User member, List<String> statuses);
    @Query("SELECT b FROM Borrowing b WHERE b.bookCopy.book = :book")
    List<Borrowing> findByBook(@Param("book") Book book);
    @Query("SELECT b FROM Borrowing b " +
            "WHERE REPLACE(LOWER(b.member.fullName), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.member.identifier), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.bookCopy.book.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.bookCopy.book.isbn), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.bookCopy.book.cote), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.bookCopy.book.author), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) ")
    List<Borrowing> searchBorrowings(@Param("keyword") String keyword);
    @Query("SELECT b FROM Borrowing b " +
            "WHERE (" +
            "  REPLACE(LOWER(b.member.fullName), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR REPLACE(LOWER(b.member.identifier), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR REPLACE(LOWER(b.bookCopy.book.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR REPLACE(LOWER(b.bookCopy.book.isbn), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR REPLACE(LOWER(b.bookCopy.book.cote), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR REPLACE(LOWER(b.bookCopy.book.author), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%'))" +
            ") " +
            "AND (:status IS NULL OR b.status = :status)")
    List<Borrowing> searchBorrowingsAndStatus(@Param("keyword") String keyword, @Param("status") String status);

    Borrowing findByBookCopyAndStatusIn(BookCopy copy, List<String> pickedUp);
}
