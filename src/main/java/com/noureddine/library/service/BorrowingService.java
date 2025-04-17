package com.noureddine.library.service;

import com.noureddine.library.dto.BorrowRequest;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import com.noureddine.library.repository.BorrowingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BorrowingService(BorrowingRepository borrowingRepository, BookCopyRepository bookCopyRepository, BookRepository bookRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }

    public List<Borrowing> getAll() throws NotFoundException {
        List<Borrowing> borrowings = borrowingRepository.findAll();
        if(borrowings.isEmpty()){
            throw new NotFoundException("There are no borrowings");
        }
        return borrowings;
    }
    public void borrow(BorrowRequest request) throws NotFoundException {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("The book not found"));
        //BookCopy availableCopy = bookCopyRepository.findFirstByBookIdAndAvailableTrue(book.getId())
          //      .orElseThrow(() -> new NotFoundException("No available copy of this book"));
        //choose which copy to borrow and save the borrowing
        List<BookCopy> copies = bookCopyRepository.findByBookId(book.getId());
        for(int i = copies.size() - 1; i >= 0; i--){
            if(copies.get(i).isAvailable()){
                Borrowing borrowing = new Borrowing(
                        copies.get(i).getInventoryNumber(),
                        request.getMemberId(),
                        request.getPickupDate(),
                        request.getReturnDate()
                );
                //change the availability of the book copy
                copies.get(i).setAvailable(false);
                bookCopyRepository.save(copies.get(i));
                //make the book unavailable if all the copies are unavailable
                if(i == 0){
                    book.setAvailable(false);
                    bookRepository.save(book);
                }
                //save the new borrowing
                borrowingRepository.save(borrowing);
                return;
            }
        }
    }
    public void removeBorrowingById(Long borrowingId) throws NotFoundException {
        Optional<Borrowing> borrowingOptional = borrowingRepository.findById(borrowingId);
        if(borrowingOptional.isEmpty()){
            throw new NotFoundException("The borrowing not found");
        }
        borrowingRepository.deleteById(borrowingId);
        Borrowing borrowing = borrowingOptional.get();

        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findById(borrowing.getInventoryNumber());
        if(bookCopyOptional.isEmpty()){
            throw new NotFoundException("The book copy not found");
        }
        BookCopy bookCopy = bookCopyOptional.get();
        bookCopy.setAvailable(true);
        bookCopyRepository.save(bookCopy);

        Optional<Book> bookOptional = bookRepository.findById(bookCopy.getBookId());
        if(bookOptional.isEmpty()){
            throw new NotFoundException("Book not found");
        }
        Book book = bookOptional.get();
        if(book.isAvailable()){
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }
    public void removeBorrowingByInventoryNumber(String inventoryNumber) throws NotFoundException {
        Optional<Borrowing> borrowingOptional = borrowingRepository.findByInventoryNumber(inventoryNumber);
        if(borrowingOptional.isEmpty()){
            throw new NotFoundException("The borrowing not found");
        }
        borrowingRepository.deleteByInventoryNumber(inventoryNumber);
        Borrowing borrowing = borrowingOptional.get();
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findById(borrowing.getInventoryNumber());
        if(bookCopyOptional.isEmpty()){
            throw new NotFoundException("The book copy not found");
        }
        BookCopy bookCopy = bookCopyOptional.get();
        bookCopy.setAvailable(true);
        bookCopyRepository.save(bookCopy);

        Optional<Book> bookOptional = bookRepository.findById(bookCopy.getBookId());
        if(bookOptional.isEmpty()){
            throw new NotFoundException("Book not found");
        }
        Book book = bookOptional.get();
        if(book.isAvailable()){
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }
    public Borrowing getBorrowingByInvNumber(String inventoryNumber) throws NotFoundException {
        Optional<Borrowing> borrowing = borrowingRepository.findByInventoryNumber(inventoryNumber);
        if(borrowing.isEmpty()){
            throw new NotFoundException("The borrowing not found");
        }
        return  borrowing.get();
    }
    public List<Borrowing> getBorrowingsByMemberId(Long memberId) throws NotFoundException {
        List<Borrowing> borrowings = borrowingRepository.findByMemberId(memberId);
        if(borrowings.isEmpty()){
            throw new NotFoundException("There are no borrowings for this member");
        }
        return borrowings;
    }
    public void confirmBookPickup(String inventoryNumber) throws NotFoundException {
        Optional<Borrowing> borrowingOptional = borrowingRepository.findByInventoryNumber(inventoryNumber);
        if(borrowingOptional.isEmpty()){
            throw new NotFoundException("The borrowing not found");
        }
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findById(inventoryNumber);
        if(bookCopyOptional.isEmpty()){
            throw new NotFoundException("The book copy not found");
        }
        BookCopy bookCopy = bookCopyOptional.get();
        bookCopy.setAvailable(false);
        bookCopyRepository.save(bookCopy);
        Borrowing borrowing = borrowingOptional.get();
        borrowing.setPickUpDate(LocalDate.now());
        borrowingRepository.save(borrowing);
        Optional<Book> bookOptional = bookRepository.findById(bookCopy.getBookId());
        if(bookOptional.isEmpty()){
            throw new NotFoundException("Book bot found");
        }
        Book book = bookOptional.get();
        if(book.isAvailable()){
            book.setAvailable(false);
            bookRepository.save(book);
        }
    }
    public void confirmBookReturn(String inventoryNumber) throws NotFoundException {
        Optional<Borrowing> borrowingOptional = borrowingRepository.findByInventoryNumber(inventoryNumber);
        if (borrowingOptional.isEmpty()) {
            throw new NotFoundException("The borrowing not found");
        }
        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findById(inventoryNumber);
        if (bookCopyOptional.isEmpty()) {
            throw new NotFoundException("The book copy not found");
        }
        BookCopy bookCopy = bookCopyOptional.get();
        bookCopy.setAvailable(true);
        bookCopyRepository.save(bookCopy);
        Borrowing borrowing = borrowingOptional.get();
        borrowing.setReturnDate(LocalDate.now());
        borrowingRepository.save(borrowing);
        Optional<Book> bookOptional = bookRepository.findById(bookCopy.getBookId());
        if(bookOptional.isEmpty()){
            throw new NotFoundException("Book bot found");
        }
        Book book = bookOptional.get();
        if(book.isAvailable()){
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }
}
