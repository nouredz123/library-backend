package com.noureddine.library.service;

import com.noureddine.library.dto.BookCopyRequest;
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
    public void addBookCopy(Long bookId, int numberOfCopies) throws NotFoundException {
        Book book = bookRepository.findById(bookId).orElseThrow(()-> new NotFoundException("Book not found"));
        int numOfCop = book.getNumberOfCopies();
        for (int i = 1 + numOfCop; i <= numOfCop+ numberOfCopies; i++ ){
            BookCopy copy = new BookCopy(
                    book.getCote() + "." + i,
                    book,
                    true
            );
            bookCopyRepository.save(copy);
        }
        book.setNumberOfCopies(numOfCop + numberOfCopies);
        book.setAvailableCopies(book.getAvailableCopies() + numberOfCopies);
        bookRepository.save(book);
    }
    public void removeBookCopy(String inventoryNumber) throws NotFoundException {
        if(!bookCopyRepository.existsById(inventoryNumber)){
            throw new NotFoundException("The book copy not found");
        }
        bookCopyRepository.deleteById(inventoryNumber);
    }
}
