package com.noureddine.library.controller;

import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import com.noureddine.library.repository.BorrowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dev")
public class DummyDataController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @GetMapping("/seed")
    public ResponseEntity<String> seedDatabase() {
        // Create dummy books
        Book book1 = new Book( "Effective Java", "Joshua Bloch", "Addison-Wesley", "2018", true, "https://example.com/java.jpg");
        Book book2 = new Book( "Clean Code", "Robert C. Martin", "Prentice Hall", "2008", true, "https://example.com/clean.jpg");
        bookRepository.saveAll(List.of(book1, book2));

        // Create dummy book copies
        BookCopy copy1 = new BookCopy("INV001", book1.getId(), true);
        BookCopy copy2 = new BookCopy("INV002", book1.getId(), false);
        BookCopy copy3 = new BookCopy("INV003", book2.getId(), true);
        bookCopyRepository.saveAll(List.of(copy1, copy2, copy3));

        // Create dummy borrowings
        Borrowing borrowing1 = new Borrowing("INV002", 1001L, LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 10));
        borrowingRepository.save(borrowing1);

        return ResponseEntity.ok("Database seeded with dummy data!");
    }
}
