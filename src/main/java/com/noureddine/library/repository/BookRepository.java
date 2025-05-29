package com.noureddine.library.repository;

import com.noureddine.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByDepartment(String department);
    Boolean existsByCote(String cote);
    Boolean existsByIsbn(String isbn);
    List<Book> findByDepartmentAndAvailable(String upperCase, Boolean available);
    List<Book> findByAvailable(Boolean available);
    @Query("SELECT b FROM Book b " +
            "WHERE ( " +
            "REPLACE(LOWER(b.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.author), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR REPLACE(LOWER(b.editionYear), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "OR b.isbn LIKE CONCAT('%', :keyword, '%') " +
            "OR b.cote LIKE CONCAT('%', :keyword, '%') " +
            ") " +
            "AND (:available IS NULL OR b.available = :available)")
    List<Book> searchBooks(@Param("keyword") String keyword, @Param("available") Boolean available);
    @Query("SELECT b FROM Book b " +
            "WHERE REPLACE(LOWER(b.title), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "AND (:available IS NULL OR b.available = :available)")
    List<Book> searchBooksByTitle(@Param("keyword") String keyword, @Param("available") Boolean available);

    @Query("SELECT b FROM Book b " +
            "WHERE REPLACE(LOWER(b.author), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "AND (:available IS NULL OR b.available = :available)")
    List<Book> searchBooksByAuthor(@Param("keyword") String keyword, @Param("available") Boolean available);

    @Query("SELECT b FROM Book b " +
            "WHERE REPLACE(LOWER(b.isbn), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "AND (:available IS NULL OR b.available = :available)")
    List<Book> searchBooksByIsbn(@Param("keyword") String keyword, @Param("available") Boolean available);

    @Query("SELECT b FROM Book b " +
            "WHERE REPLACE(LOWER(b.cote), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "AND (:available IS NULL OR b.available = :available)")
    List<Book> searchBooksByCote(@Param("keyword") String keyword, @Param("available") Boolean available);

    @Query("SELECT b FROM Book b " +
            "WHERE REPLACE(LOWER(b.editionYear), ' ', '') LIKE LOWER(CONCAT('%', REPLACE(:keyword, ' ', ''), '%')) " +
            "AND (:available IS NULL OR b.available = :available)")
    List<Book> searchBooksByEditionYear(@Param("keyword") String keyword, @Param("available") Boolean available);


}
