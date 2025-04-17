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
        Map<Long, List<String>> groupedInventory = new HashMap<>();
        for (BookCopy copy : copies) {
            groupedInventory
                    .computeIfAbsent(copy.getBookId(), k -> new ArrayList<>())
                    .add(copy.getInventoryNumber());
        }
        List<CopiesResponse> responses = new ArrayList<>();
        for (Map.Entry<Long, List<String>> entry : groupedInventory.entrySet()) {
            Optional<Book> bookOpt = bookRepository.findById(entry.getKey());
            bookOpt.ifPresent(book -> responses.add(new CopiesResponse(book, entry.getValue())));
        }
        return responses;
    }
    public void addBookCopy(BookCopy copyRequest) throws NotFoundException {
        copyRequest.setAvailable(true);
        Optional<BookCopy> copyOptional  = bookCopyRepository.findById(copyRequest.getInventoryNumber());
        if(copyOptional.isPresent()){
                if(!copyOptional.get().getBookId().equals(copyRequest.getBookId())){
                    throw new NotFoundException("The book copy '" + copyRequest.getInventoryNumber() + "' already exists for another book");
                }
        }
        bookCopyRepository.save(copyRequest);
        Optional<Book> bookOptional = bookRepository.findById(copyRequest.getBookId());
        bookOptional.ifPresent(book -> {
            Boolean available = book.isAvailable();
            if(available == null || !available){
                book.setAvailable(true);
                bookRepository.save(book);
            }
        });
    }
    public void removeBookCopy(String inventoryNumber) throws NotFoundException {
        if(!bookCopyRepository.existsById(inventoryNumber)){
            throw new NotFoundException("The book copy not found");
        }
        bookCopyRepository.deleteById(inventoryNumber);
    }
}
