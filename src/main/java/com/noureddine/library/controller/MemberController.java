package com.noureddine.library.controller;

import com.noureddine.library.dto.BorrowRequest;
import com.noureddine.library.dto.PageResponse;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.entity.User;
import com.noureddine.library.service.BookService;
import com.noureddine.library.service.BorrowingService;
import com.noureddine.library.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/member")
public class MemberController {
    private final BookService bookService;
    private final BorrowingService borrowingService;
    private final UserService userService;

    public MemberController(BookService bookService, BorrowingService borrowingService, UserService userService) {
        this.bookService = bookService;
        this.borrowingService = borrowingService;
        this.userService = userService;
    }

    @GetMapping("/books")
    public PageResponse<Book> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String searchBy,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return bookService.getBooks(keyword, searchBy, department, available, page, size, sortBy, direction);
    }

    @GetMapping("/{memberId}/borrowings")
    public PageResponse<Borrowing> getBorrowings(
            @PathVariable Long memberId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "true") boolean paged
    ) {
        if(!paged){
            return borrowingService.getAllBorrowingsByMemberId(memberId);
        }
        return borrowingService.getBorrowingsByMemberId(memberId, status, page, size, sortBy, direction);
    }

    @GetMapping("/{memberId}/info")
    public ResponseEntity<User> getInfo(@PathVariable Long memberId) {
        User user = userService.getInfo(memberId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/borrow")
    public ResponseEntity<Map<String, String>> addBorrowing(@RequestBody BorrowRequest request) {
        borrowingService.borrow(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Borrowing done Successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/borrowing/{borrowingId}")
    public ResponseEntity<Map<String, String>> cancelBorrowing(@PathVariable Long borrowingId) {
        borrowingService.removeBorrowingById(borrowingId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Borrowing cancelled successfully");
        return ResponseEntity.ok(response);
    }
}
