package com.noureddine.library.service;

import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import com.noureddine.library.repository.BorrowingRepository;
import com.noureddine.library.repository.UserRepository;
import com.noureddine.library.utils.SearchUtils;
import com.noureddine.library.utils.SortUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;

    public BookCopyService(BookCopyRepository bookCopyRepository, BookRepository bookRepository, BorrowingRepository borrowingRepository, UserRepository userRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
    }
    public List<BookCopy> getAll() {
        List<BookCopy> books = bookCopyRepository.findAll();
        if(books.isEmpty()){
            throw new NotFoundException("No books found on the database");
        }
        return books;
    }

    public void addBookCopy(Long bookId, int numberOfCopies) {
        //search for the book on the database
        List<Book> books = bookRepository.findAll();
        if(books.isEmpty()){
            throw new NotFoundException("No books found");
        }
        SortUtils.heapSort(books, Book::getId);
        Book book = SearchUtils.binarySearch(books, bookId, Book::getId);
        if(book == null){
            throw new NotFoundException("Book not found");
        }
        //create and save new copies to the database
        int numOfCop = book.getNumberOfCopies();
        for (int i = 1 + numOfCop; i <= numOfCop+ numberOfCopies; i++ ){
            BookCopy copy = new BookCopy(
                    book.getCote() + "." + i,
                    book,
                    true
            );
            bookCopyRepository.save(copy);
        }
        //update the number of copies and available copies for the book
        book.setNumberOfCopies(numOfCop + numberOfCopies);
        book.setAvailableCopies(book.getAvailableCopies() + numberOfCopies);
        bookRepository.save(book);
    }

    public void removeBookCopy(String inventoryNumber) {
        List<BookCopy> copies = bookCopyRepository.findAll();
        if(copies.isEmpty()){
            throw new NotFoundException("No BookCopies found");
        }
        SortUtils.heapSort(copies, BookCopy::getInventoryNumber);
        BookCopy copy = SearchUtils.binarySearch(copies, inventoryNumber, BookCopy::getInventoryNumber);
        if(copy == null){
            throw new NotFoundException("BookCopy not found");
        }
        //get the borrowing for that copy with one of this status ("PICKED_UP","OVERDUE","PENDING")
        Borrowing borrowing = borrowingRepository.findByBookCopyAndStatusIn(copy, List.of("PICKED_UP","OVERDUE","PENDING" ));
        if(borrowing != null){
            if(borrowing.getStatus().equalsIgnoreCase("PICKED_UP")
                    || borrowing.getStatus().equalsIgnoreCase("OVERDUE")){
                throw new IllegalArgumentException("The copy is picked up and not yet returned");
            } else if (borrowing.getStatus().equalsIgnoreCase("PENDING")) {
                borrowingRepository.delete(borrowing);
                //get all users from database
                List<User> allUsers = userRepository.findAll();
                //check if the users list is empty, if it is throw an exception
                if(allUsers.isEmpty()){
                    throw new NotFoundException("There are no users.");
                }
                //sort the list and then search for the member with the provided id
                SortUtils.heapSort(allUsers, User::getId);
                User user = SearchUtils.binarySearch(allUsers, borrowing.getMember().getId(), User::getId);
                //if the member not found throw an exception
                if(user == null){
                    throw new NotFoundException("The user not found");
                }
                user.setNumberOfBorrowings(user.getNumberOfBorrowings() - 1);
                userRepository.save(user);
            }
        }
        //update the number of copies and available copies and available for the book
        List<Book> books = bookRepository.findAll();
        if(books.isEmpty()){
            throw new NotFoundException("No books found");
        }
        SortUtils.heapSort(books, Book::getId);
        Book book = SearchUtils.binarySearch(books, copy.getBook().getId(), Book::getId);
        if(book == null){
            throw new NotFoundException("Book not found");
        }
        book.setNumberOfCopies(book.getNumberOfCopies() - 1);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if(book.getAvailableCopies() == 0){
            book.setAvailable(false);
        }
        bookRepository.save(book);
        bookCopyRepository.deleteById(inventoryNumber);
    }
}
