package com.noureddine.library.service;

import com.noureddine.library.dto.BorrowRequest;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import com.noureddine.library.repository.BorrowingRepository;
import com.noureddine.library.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BorrowingService(BorrowingRepository borrowingRepository, BookCopyRepository bookCopyRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
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
        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new NotFoundException("There is no user with the provided id"));
        //choose which copy to borrow and save the borrowing
        List<BookCopy> copies = bookCopyRepository.findByBookId(book.getId());
        if(copies.isEmpty()){
            throw new NotFoundException("There is no copies for this book");
        }
        for(int i = copies.size() - 1; i >= 0; i--){
            if(borrowingRepository.existsByBookCopyAndMember(copies.get(i), member)){
                throw new NotFoundException("book already borrowed by this user");
            }
        }
        for(int i = copies.size() - 1; i >= 0; i--){
            if(copies.get(i).isAvailable()){
                Borrowing borrowing = new Borrowing(
                        copies.get(i),
                        member,
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

        Optional<BookCopy> bookCopyOptional = bookCopyRepository.findById(borrowing.getBookCopy().getInventoryNumber());
        if(bookCopyOptional.isEmpty()){
            throw new NotFoundException("The book copy not found");
        }
        BookCopy bookCopy = bookCopyOptional.get();
        bookCopy.setAvailable(true);
        bookCopyRepository.save(bookCopy);


        Book book = bookCopy.getBook();
        if(book.isAvailable()){
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }
    public void removeBorrowingByInventoryNumber(String inventoryNumber) throws NotFoundException {
        BookCopy bookCopy = bookCopyRepository.findById(inventoryNumber).orElseThrow(()-> new NotFoundException("Book copy not found"));
        Optional<Borrowing> borrowingOptional = borrowingRepository.findByBookCopy(bookCopy);
        if(borrowingOptional.isEmpty()){
            throw new NotFoundException("The borrowing not found");
        }
        borrowingRepository.deleteByBookCopy(bookCopy);
        Borrowing borrowing = borrowingOptional.get();
        bookCopy.setAvailable(true);
        bookCopyRepository.save(bookCopy);

        Book book = bookCopy.getBook();
        if(book.isAvailable()){
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }
    public Borrowing getBorrowingByInvNumber(String inventoryNumber) throws NotFoundException {
        BookCopy bookCopy = bookCopyRepository.findById(inventoryNumber).orElseThrow(()-> new NotFoundException("Book copy not found"));
        Optional<Borrowing> borrowing = borrowingRepository.findByBookCopy(bookCopy);
        if(borrowing.isEmpty()){
            throw new NotFoundException("The borrowing not found");
        }
        return  borrowing.get();
    }
    public List<Borrowing> getBorrowingsByMemberId(Long memberId) throws NotFoundException {
        User member = userRepository.findById(memberId).orElseThrow(()-> new NotFoundException("Member not found"));
        List<Borrowing> borrowings = borrowingRepository.findByMember(member);
        if(borrowings.isEmpty()){
            throw new NotFoundException("There are no borrowings for this member");
        }
        return borrowings;
    }
    public void confirmBookPickup(String inventoryNumber) throws NotFoundException {
        BookCopy bookCopy = bookCopyRepository.findById(inventoryNumber).orElseThrow(()-> new NotFoundException("Book copy not found"));
        Optional<Borrowing> borrowingOptional = borrowingRepository.findByBookCopy(bookCopy);
        if(borrowingOptional.isEmpty()){
            throw new NotFoundException("The borrowing not found");
        }
        bookCopy.setAvailable(false);
        bookCopyRepository.save(bookCopy);
        Borrowing borrowing = borrowingOptional.get();
        borrowing.setPickUpDate(LocalDate.now());
        borrowingRepository.save(borrowing);
        Book book = bookCopy.getBook();
        if(book.isAvailable()){
            book.setAvailable(false);
            bookRepository.save(book);
        }
    }
    public void confirmBookReturn(String inventoryNumber) throws NotFoundException {
        BookCopy bookCopy = bookCopyRepository.findById(inventoryNumber).orElseThrow(()-> new NotFoundException("Book copy not found"));
        Optional<Borrowing> borrowingOptional = borrowingRepository.findByBookCopy(bookCopy);
        if (borrowingOptional.isEmpty()) {
            throw new NotFoundException("The borrowing not found");
        }
        bookCopy.setAvailable(true);
        bookCopyRepository.save(bookCopy);
        Borrowing borrowing = borrowingOptional.get();
        borrowing.setReturnDate(LocalDate.now());
        borrowingRepository.save(borrowing);
        Book book = bookCopy.getBook();
        if(book.isAvailable()){
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }
}
