package com.noureddine.library.controller;

import com.noureddine.library.dto.BookRequest;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import com.noureddine.library.repository.BorrowingRepository;
import com.noureddine.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/dev")
public class DummyDataController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @GetMapping("/seed")
    public ResponseEntity<String> seedDatabase() throws NotFoundException {
        List<Book> books = new ArrayList<>();
        String[] departments = {"Computer Science", "Mathematics", "Chemistry", "Physics"};
        Random random = new Random();

        // Create and save 10 books
        for (int i = 1; i <= 10; i++) {
            BookRequest request = new BookRequest(
                    "Book Title " + i,
                    "Author " + i,
                    "Publisher " + i,
                    "20" + (10 + i),
                    "1545" + i + random.nextInt() % 25 * 10,
                    "004-15" + i + random.nextInt() % 10,
                    i + random.nextInt() % 10,
                    "https://example.com/book" + i + ".jpg",
                    departments[random.nextInt(departments.length)]
            );
            bookService.addBook(request);
        }
        bookRepository.saveAll(books);
        return ResponseEntity.ok("Database seeded with dummy data!");
    }


}
