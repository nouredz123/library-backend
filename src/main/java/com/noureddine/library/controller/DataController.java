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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/dev")
public class DataController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BorrowingRepository borrowingRepository;

    @GetMapping("/seed")
    public ResponseEntity<String> seedDatabase() throws Exception {
        // Load the JSON file from resources
        ClassPathResource resource = new ClassPathResource("books.json");
        ObjectMapper mapper = new ObjectMapper();

        // Deserialize JSON array to List<BookRequest>
        List<BookRequest> books = mapper.readValue(resource.getInputStream(), new TypeReference<List<BookRequest>>() {});

        // Add each book to DB
        for (BookRequest request : books) {
            bookService.addBook(request);
        }

        return ResponseEntity.ok("Database seeded from file!");
    }


}