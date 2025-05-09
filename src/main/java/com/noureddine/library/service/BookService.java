package com.noureddine.library.service;

import com.noureddine.library.dto.PageResponse;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.dto.BookRequest;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    public BookService(BookRepository bookRepository, BookCopyRepository bookCopyRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    public Page<Book> getBooks(String department, int page, int size, String sortBy, String direction) throws NotFoundException {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> booksPage;

        if (department != null && !department.isEmpty()) {
            booksPage = bookRepository.findByDepartment(department, pageable);
        } else {
            booksPage = bookRepository.findAll(pageable);
        }

        if (booksPage.isEmpty()) {
            throw new NotFoundException("No books found in the database");
        }

        return booksPage;
    }
    public Page<Book> searchBooks(String keyword, int page, int size, String sortBy, String direction) throws NotFoundException {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> booksPage = bookRepository.searchBooks(keyword, pageable);

        if (booksPage.isEmpty()) {
            throw new NotFoundException("No books found matching your search");
        }

        return booksPage;
    }
    public void addBook(BookRequest bookRequest) throws NotFoundException {
        boolean available = !bookRequest.getInventoryNumbers().isEmpty();
        Book book = new Book(
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getPublisher(),
                bookRequest.getEditionYear(),
                bookRequest.getIsbn(),
                bookRequest.getNumberOfCopies(),
                available,
                bookRequest.getCoverUrl(),
                bookRequest.getDepartment(),
                LocalDate.now()
        );
        Book savedBook = bookRepository.save(book);
        if(bookRequest.getInventoryNumbers().isEmpty()){
            return;
        }
        //check for the copies inventory numbers if they exist for other books
        List<BookCopy> existingCopies  = bookCopyRepository.findAllById(bookRequest.getInventoryNumbers());
        if(!existingCopies.isEmpty()){
            for(BookCopy existingCopy : existingCopies){
                if(!existingCopy.getBook().getId().equals(savedBook.getId())){
                    throw new NotFoundException("The book copy '" + existingCopy.getInventoryNumber() + "' already exists for another book");
                }
            }
        }
        //save the book copies
        for(String inventoryNumber : bookRequest.getInventoryNumbers()){
            BookCopy copy = new BookCopy(
                    inventoryNumber,
                    savedBook,
                    true,
                    "AVAILABLE"
            );
            bookCopyRepository.save(copy);
        }
    }
    public void removeBook(Long bookId) throws NotFoundException {
        if(!bookRepository.existsById(bookId)){
            throw new NotFoundException("The book not found");
        }
        bookRepository.deleteById(bookId);
        List<BookCopy> bookCopies = bookCopyRepository.findByBookId(bookId);
        if(!bookCopies.isEmpty()){
            for(BookCopy copy : bookCopies){
                bookCopyRepository.deleteById(copy.getInventoryNumber());
            }
        }
    }
}
