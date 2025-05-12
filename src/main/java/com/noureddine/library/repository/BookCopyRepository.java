package com.noureddine.library.repository;

import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, String> {
    List<BookCopy> findByBook(Book book);
    Long countByAvailable(Boolean available);
    Optional<BookCopy> findFirstByBookIdAndAvailableTrue(Long bookId);
}
