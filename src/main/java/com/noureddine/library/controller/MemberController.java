package com.noureddine.library.controller;

import com.noureddine.library.dto.BorrowRequest;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.service.BookService;
import com.noureddine.library.service.BorrowingService;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/member")
public class MemberController {
    private final BookService bookService;
    private final BorrowingService borrowingService;

    public MemberController(BookService bookService, BorrowingService borrowingService) {
        this.bookService = bookService;
        this.borrowingService = borrowingService;
    }

    @GetMapping("/books")
    public Page<Book> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) throws NotFoundException {
        return bookService.getBooks(page, size, sortBy, direction);
    }
    @GetMapping("/{memberId}/borrowings")
    public List<Borrowing> getBorrowings(@PathVariable Long memberId) throws NotFoundException, BadRequestException {
        if (memberId == null) {
            throw new BadRequestException("memberId is required");
        }
        return borrowingService.getBorrowingsByMemberId(memberId);
    }
    @DeleteMapping("/borrowings/{borrowingId}")
    public ResponseEntity<Map<String, String>> cancelBorrowing(@PathVariable Long borrowingId) throws NotFoundException {
        borrowingService.removeBorrowingById(borrowingId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Borrowing cancelled successfully");
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/borrowings/by-inventory/{inventoryNumber}")
    public ResponseEntity<Map<String, String>> cancelBorrowingByInventoryNumber(@PathVariable String inventoryNumber) throws NotFoundException {
        borrowingService.removeBorrowingByInventoryNumber(inventoryNumber);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Borrowing cancelled successfully");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/borrow")
    public ResponseEntity<Map<String, String>> addBorrowing(@RequestBody BorrowRequest request) throws NotFoundException {
        borrowingService.borrow(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Borrowing done Successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
