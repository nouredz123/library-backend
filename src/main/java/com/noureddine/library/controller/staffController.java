package com.noureddine.library.controller;

import com.noureddine.library.dto.CopiesResponse;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.dto.BookRequest;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.service.BookCopyService;
import com.noureddine.library.service.BookService;
import com.noureddine.library.service.BorrowingService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/staff")
public class staffController {
    private final BookService bookService;
    private final BorrowingService borrowingService;
    private final BookCopyService bookCopyService;

    public staffController(BookService bookService, BorrowingService borrowingService, BookCopyService bookCopyService) {
        this.bookService = bookService;
        this.borrowingService = borrowingService;
        this.bookCopyService = bookCopyService;
    }
    public Page<Book> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) throws NotFoundException {
        return bookService.getBooks(page, size, sortBy, direction);
    }
    @GetMapping("/bookCopies")
    public List<CopiesResponse> getBookCopies() throws NotFoundException {
        return bookCopyService.getCopies();
    }
    @GetMapping("/borrowings")
    public List<Borrowing> getBorrowings() throws NotFoundException {
        return borrowingService.getAll();
    }
    @GetMapping("/BorrowingsByMemberId")
    public List<Borrowing> getMemberBorrowings(@RequestParam Long memberId) throws NotFoundException {
        return borrowingService.getBorrowingsByMemberId(memberId);
    }
    @GetMapping("/BorrowingByInventoryNumber")
    public Borrowing getBorrowing(@RequestParam String inventoryNumber) throws NotFoundException {
        return borrowingService.getBorrowingByInvNumber(inventoryNumber);
    }

    @PostMapping("/book")
    public ResponseEntity<Map<String, String>> addBook(@RequestBody BookRequest request) throws NotFoundException {
        bookService.addBook(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book added successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/bookCopy")
    public ResponseEntity<Map<String, String>> addBookCopy(@RequestBody BookCopy bookcopy) throws NotFoundException {
        bookCopyService.addBookCopy(bookcopy);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book copy added successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/book/{id}")
    public ResponseEntity<Map<String, String>> removeBook(@PathVariable Long id) throws NotFoundException {
        bookService.removeBook(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @DeleteMapping("/bookCopy/{inventoryNumber}")
    public ResponseEntity<Map<String, String>> removeBookCopy(@PathVariable String inventoryNumber) throws NotFoundException {
        bookCopyService.removeBookCopy(inventoryNumber);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book copy added successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PatchMapping("/confirmPickup/{inventoryNumber}")
    public ResponseEntity<Map<String, String>> confirmBookPickup(@PathVariable String inventoryNumber) throws NotFoundException {
        borrowingService.confirmBookPickup(inventoryNumber);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book picked up successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PatchMapping("/confirmReturn/{inventoryNumber}")
    public ResponseEntity<Map<String, String>> confirmBookReturn(@PathVariable String inventoryNumber) throws NotFoundException {
        borrowingService.confirmBookReturn(inventoryNumber);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book returned successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
