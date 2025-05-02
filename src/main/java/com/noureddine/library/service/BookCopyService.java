package com.noureddine.library.service;

import com.noureddine.library.dto.CopiesResponse;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookRepository bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }
    public List<BookCopy> getAll() throws NotFoundException {
        List<BookCopy> books = bookCopyRepository.findAll();
        if(books.isEmpty()){
            throw new NotFoundException("No books found on the database");
        }
        return books;
    }
    public List<CopiesResponse> getCopies() throws NotFoundException {
        List<BookCopy> copies = bookCopyRepository.findAll();
        if(copies.isEmpty()){
            throw new NotFoundException("No books found on the database");
        }
        Map<Book, List<String>> groupedInventory = new HashMap<>();
        for (BookCopy copy : copies) {
            groupedInventory
                    .computeIfAbsent(copy.getBook(), k -> new ArrayList<>())
                    .add(copy.getInventoryNumber());
        }
        List<CopiesResponse> responses = new ArrayList<>();
        for (Map.Entry<Book, List<String>> entry : groupedInventory.entrySet()) {
            responses.add(new CopiesResponse(entry.getKey(), entry.getValue()));
        }
        return responses;
    }
    public void addBookCopy(BookCopy copyRequest) throws NotFoundException {
        // Check if the inventory number already exists
        Optional<BookCopy> copyOptional  = bookCopyRepository.findById(copyRequest.getInventoryNumber());
        if(copyOptional.isPresent()){
                Book existingBook = copyOptional.get().getBook();
                //if they not for the same book
            if (!existingBook.getId().equals(copyRequest.getBook().getId())) {
                throw new NotFoundException("The book copy '" + copyRequest.getInventoryNumber() + "' already exists for another book");
            }
        }
        // Ensure the book exists in the database
        Long bookId = copyRequest.getBook().getId();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book with id " + bookId + " not found"));

        BookCopy bookCopy = new BookCopy(copyRequest.getInventoryNumber(), book, true, copyRequest.getStatus());

        // Save the copy
        bookCopyRepository.save(copyRequest);

        // If the book is currently marked unavailable, mark it available
        if (!book.isAvailable()) {
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }
    public void removeBookCopy(String inventoryNumber) throws NotFoundException {
        if(!bookCopyRepository.existsById(inventoryNumber)){
            throw new NotFoundException("The book copy not found");
        }
        bookCopyRepository.deleteById(inventoryNumber);
    }
}
