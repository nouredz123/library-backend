package com.noureddine.library.repository;

import com.noureddine.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByDepartment(String department, Pageable pageable);
    @Query("SELECT b FROM Book b " +
            "WHERE REPLACE(LOWER(b.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.author), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR b.isbn LIKE CONCAT('%', :keyword, '%')" +
            "OR b.cote LIKE CONCAT('%', :keyword, '%')")
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT b FROM Book b " +
            "WHERE ( " +
            "  REPLACE(LOWER(b.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR REPLACE(LOWER(b.author), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR b.isbn LIKE CONCAT('%', :keyword, '%') " +
            "  OR b.cote LIKE CONCAT('%', :keyword, '%') " +
            ") " +
            "AND (:available IS NULL OR b.available = :available)")
    Page<Book> searchBooksAndAvailable(@Param("keyword") String keyword, @Param("available") Boolean available, Pageable pageable);


    Boolean existsByCote(String cote);

    Boolean existsByIsbn(String isbn);

    Page<Book> findByDepartmentAndAvailable(String department, Boolean available, Pageable pageable);

    Page<Book> findByAvailable(Boolean available, Pageable pageable);
}
