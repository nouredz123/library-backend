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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<Book> books = new ArrayList<>();
        List<BookCopy> bookCopies = new ArrayList<>();
        List<Borrowing> borrowings = new ArrayList<>();
        String[] departments = {"Computer Science", "Mathematics", "Chemistry", "Physics"};
        Random random = new Random();

        // Create and save 10 books
        for (int i = 1; i <= 10; i++) {
            Book book = new Book(
                    "Book Title " + i,
                    "Author " + i,
                    "Publisher " + i,
                    "20" + (10 + i),
                    true,
                    "https://example.com/book" + i + ".jpg",
                    departments[random.nextInt(departments.length)]
            );
            books.add(book);
        }
        bookRepository.saveAll(books);

        // Add 3 copies per book: 2 available, 1 unavailable
        int inventoryIndex = 1;
        for (Book book : books) {
            for (int j = 1; j <= 3; j++) {
                boolean isAvailable = (j != 2); // Make 2nd copy unavailable
                BookCopy copy = new BookCopy(
                        "INV" + String.format("%03d", inventoryIndex++),
                        book,
                        isAvailable
                );
                bookCopies.add(copy);
            }
        }
        bookCopyRepository.saveAll(bookCopies);

        // Create sample borrowings (match unavailable copies)

        borrowingRepository.saveAll(borrowings);

        return ResponseEntity.ok("Database seeded with dummy data!");
    }


}
