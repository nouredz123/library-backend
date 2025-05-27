package com.noureddine.library.controller;

import com.noureddine.library.dto.*;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api/staff")
public class staffController {
    private final BookService bookService;
    private final BookCopyService bookCopyService;
    private final BorrowingService borrowingService;
    private final InsightsService insightsService;
    private final UserService userService;

    public staffController(BookService bookService, BookCopyService bookCopyService, BorrowingService borrowingService, InsightsService insightsService, UserService userService) {
        this.bookService = bookService;
        this.bookCopyService = bookCopyService;
        this.borrowingService = borrowingService;
        this.insightsService = insightsService;
        this.userService = userService;
    }

    @GetMapping("/insights")
    public ResponseEntity<InsightsResponse> getInsights(){
        InsightsResponse response = insightsService.getInsights();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{staffId}/info")
    public ResponseEntity<User> getInfo(@PathVariable Long staffId) throws NotFoundException {
        User user = userService.getInfo(staffId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public PageResponse<UserResponse> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) throws NotFoundException {
        if(keyword != null){
            return userService.searchUsers(keyword, role, page, size, sortBy, direction);
        }else{
            return userService.getUsers(role, page, size, sortBy, direction);
        }
    }

    @GetMapping("/accountRequests")
    public PageResponse<UserResponse> getAccountRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) throws NotFoundException {
        if(keyword != null){
            return userService.searchAccountRequests( keyword, status, page, size, sortBy, direction);
        }else{
            return userService.getAccountRequests( status, page, size, sortBy, direction);
        }
    }

    @GetMapping("/books")
    public PageResponse<Book> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) throws NotFoundException {
        if(keyword != null){
            return bookService.searchBooks(keyword, available, page, size, sortBy, direction);
        }else{
            return bookService.getBooks(department, page, size, sortBy, direction);
        }
    }

    @GetMapping("/borrowings")
    public PageResponse<Borrowing> getBorrowings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) throws NotFoundException {
        if(keyword != null){
            return borrowingService.searchBorrowings(keyword, status, page, size, sortBy, direction);
        }else{
            return borrowingService.getAll(status, page, size, sortBy, direction);
        }
    }

    @GetMapping("/validate/cote")
    public ResponseEntity<Boolean> validateCote(@RequestParam String cote) {
        return ResponseEntity.ok(bookService.isCoteValid(cote));
    }

    @GetMapping("/validate/isbn")
    public ResponseEntity<Boolean> validateIsbn(@RequestParam String isbn){
        return ResponseEntity.ok(bookService.isIsbnValid(isbn));
    }

    @PatchMapping("/reviewBorrowRequest/{borrowingId}")
    public ResponseEntity<Map<String, String>> reviewBorrowing(@PathVariable Long borrowingId, @RequestParam String status) throws NotFoundException {
        borrowingService.reviewBorrowing(borrowingId, status);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Borrowing reviewed successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/reviewAccount/{userId}")
    public ResponseEntity<Map<String, String>> reviewAccount(@PathVariable Long userId, @RequestParam String status) throws NotFoundException {
        userService.reviewAccount(userId, status);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Account reviewed successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/book")
    public ResponseEntity<Map<String, String>> addBook(@RequestBody BookRequest request) throws NotFoundException {
        bookService.addBook(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book added successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @PostMapping("/bookCopy/{bookId}")
    public ResponseEntity<Map<String, String>> addBookCopy(@PathVariable Long bookId, @RequestParam int numberOfCopies) throws NotFoundException {
       bookCopyService.addBookCopy(bookId, numberOfCopies);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book copies added successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/book/{id}")
    public ResponseEntity<Map<String, String>> removeBook(@PathVariable Long id) throws NotFoundException {
        bookService.removeBook(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Book deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) throws NotFoundException {
        userService.deleteUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "user deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
