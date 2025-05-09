package com.noureddine.library.repository;

import com.noureddine.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByDepartment(String department, Pageable pageable);
    @Query("SELECT b FROM Book b " +
            "WHERE REPLACE(LOWER(b.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.author), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR b.isbn LIKE CONCAT('%', :keyword, '%')")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);
}
