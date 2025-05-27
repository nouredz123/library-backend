package com.noureddine.library.repository;

import com.noureddine.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByDepartment(String department);
    @Query("SELECT b FROM Book b " +
            "WHERE REPLACE(LOWER(b.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.author), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.editionYear), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR b.isbn LIKE CONCAT('%', :keyword, '%')" +
            "OR b.cote LIKE CONCAT('%', :keyword, '%')")
    List<Book> searchBooks(@Param("keyword") String keyword);
    @Query("SELECT b FROM Book b " +
            "WHERE ( " +
            "  REPLACE(LOWER(b.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR REPLACE(LOWER(b.author), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "  OR b.isbn LIKE CONCAT('%', :keyword, '%') " +
            "  OR b.cote LIKE CONCAT('%', :keyword, '%') " +
            ") " +
            "AND (:available IS NULL OR b.available = :available)")
    List<Book> searchBooksAndAvailable(@Param("keyword") String keyword, @Param("available") Boolean available);
    Boolean existsByCote(String cote);
    Boolean existsByIsbn(String isbn);
}
