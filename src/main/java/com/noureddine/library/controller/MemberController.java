package com.noureddine.library.controller;

import com.noureddine.library.dto.BorrowRequest;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.service.BookService;
import com.noureddine.library.service.BorrowingService;
import com.noureddine.library.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
    private final UserService userService;

    public MemberController(BookService bookService, BorrowingService borrowingService, UserService userService) {
        this.bookService = bookService;
        this.borrowingService = borrowingService;
        this.userService = userService;
    }
    @GetMapping("/books")
    public Page<Book> getBooks(
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            PagedResourcesAssembler<Book> assembler
    ) throws NotFoundException {
       return bookService.getBooks(department, page, size, sortBy, direction);
    }
    @GetMapping("/books/search")
    public Page<Book> searchBooks(
            @RequestParam String keyword,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) throws NotFoundException {
        return bookService.searchBooks(keyword, available, page, size, sortBy, direction);
    }
    @GetMapping("/{memberId}/info")
    public ResponseEntity<User> getInfo(@PathVariable Long memberId) throws NotFoundException {
        User user = userService.getInfo(memberId);
        return ResponseEntity.ok(user);

    }

    @GetMapping("/{memberId}/borrowings")
    public List<Borrowing> getBorrowings(@PathVariable Long memberId) throws NotFoundException, BadRequestException {
        if (memberId == null) {
            throw new BadRequestException("memberId is required");
        }
        return borrowingService.getBorrowingsByMemberId(memberId);
    }

    @PostMapping("/borrow")
    public ResponseEntity<Map<String, String>> addBorrowing(@RequestBody BorrowRequest request) throws NotFoundException {
        borrowingService.borrow(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Borrowing done Successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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

}
