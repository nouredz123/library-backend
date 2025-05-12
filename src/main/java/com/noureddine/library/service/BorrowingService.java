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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
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


    @Scheduled(cron = "0 0 1 * * ?") // Runs every day at 1 AM
    public void cancelOverduePendingBorrowingsTask() {
        cancelOverduePendingBorrowings();
        OverduePickedUpBorrowings();
        System.out.println("Overdue pending borrowings cleanup ran at: " + LocalDate.now());
    }
    public void cancelOverduePendingBorrowings() {
        List<Borrowing> pendingBorrowings = borrowingRepository.findByStatus("PENDING");

        for (Borrowing borrowing : pendingBorrowings) {
            // If pick up date is set and it’s in the past, cancel it
            if (borrowing.getPickUpDate() != null && borrowing.getPickUpDate().isBefore(LocalDate.now())) {
                borrowing.setStatus("CANCELLED");
                borrowingRepository.save(borrowing);

                // Make the book copy available again
                BookCopy bookCopy = borrowing.getBookCopy();
                bookCopy.setAvailable(true);
                bookCopyRepository.save(bookCopy);

                // Decrease member's borrow count
                User member = borrowing.getMember();
                member.setNumberOfBorrowings(member.getNumberOfBorrowings() - 1);
                userRepository.save(member);
            }
        }
    }
    public void OverduePickedUpBorrowings() {
        List<Borrowing> pendingBorrowings = borrowingRepository.findByStatus("PICKED_UP");

        for (Borrowing borrowing : pendingBorrowings) {
            if (borrowing.getPickUpDate() != null && borrowing.getPickUpDate().isBefore(LocalDate.now())) {
                borrowing.setStatus("OVERDUE");
                borrowingRepository.save(borrowing);
            }
        }
    }


    public Page<Borrowing> getAll(String status, int page, int size, String sortBy, String direction) throws NotFoundException {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Borrowing> borrowingsPage ;

        if (status != null && !status.isEmpty()) {
            borrowingsPage = borrowingRepository.findByStatus(status, pageable);
        } else {
            borrowingsPage = borrowingRepository.findAll(pageable);
        }

        if(borrowingsPage.isEmpty()){
            throw new NotFoundException("There are no borrowings");
        }
        return borrowingsPage;
    }
    public void borrow(BorrowRequest request) throws NotFoundException {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("The book not found"));
        User member = userRepository.findById(request.getMemberId())
                .orElseThrow(() -> new NotFoundException("There is no user with the provided id"));
        if(!member.getAccountStatus().equals("APPROVED")){
            throw new NotFoundException("Your account is pending approval. Please wait for confirmation.");
        }
        if(member.getNumberOfBorrowings() >= 2){
            throw new NotFoundException("You have reached the maximum number of borrowings");
        }
        //choose which copy to borrow and save the borrowing
        List<BookCopy> copies = bookCopyRepository.findByBook(book);
        if(copies.isEmpty()){
            throw new NotFoundException("There is no copies for this book");
        }
        for(int i = copies.size() - 1; i >= 0; i--){
            if (borrowingRepository.existsByBookCopyAndMemberAndStatusIn(
                    copies.get(i),
                    member,
                    List.of("PENDING", "PICKED_UP")
            )) {
                throw new NotFoundException("Book already borrowed by this user and not yet returned.");
            }
        }
        for(int i = copies.size() - 1; i >= 0; i--){
            if(copies.get(i).isAvailable()){
                Borrowing borrowing = new Borrowing(
                        copies.get(i),
                        member,
                        LocalDate.now(),
                        LocalDate.now().plusDays(3),
                        LocalDate.now().plusDays(10),
                        "PENDING"
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
                member.setNumberOfBorrowings(member.getNumberOfBorrowings() + 1);
                userRepository.save(member);
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
        borrowing.setStatus("PICKED-UP");
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
        borrowing.setStatus("RETURNED");
        borrowingRepository.save(borrowing);
        Book book = bookCopy.getBook();
        if(book.isAvailable()){
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }

    public void reviewBorrowing(Long borrowingId, String status) throws NotFoundException {
        Borrowing borrowing = borrowingRepository.findById(borrowingId).orElseThrow(()-> new NotFoundException("Borrowing not found."));

        if(status.isEmpty() || !(status.equalsIgnoreCase("PENDING") || status.equalsIgnoreCase("PICKED_UP") || status.equalsIgnoreCase("RETURNED"))){
            throw new NotFoundException("Invalid status");
        }
        borrowing.setStatus(status.toUpperCase());
        if(status.equalsIgnoreCase("PICKED_UP")){
            borrowing.setPickUpDate(LocalDate.now());
            borrowing.setReturnDate(LocalDate.now().plusDays(7));
        } else if (status.equalsIgnoreCase("RETURNED")){
            borrowing.setReturnDate(LocalDate.now());
            User member = userRepository.findById(borrowing.getMember().getId()).orElseThrow(()->new NotFoundException("User not found."));
            member.setNumberOfBorrowings(member.getNumberOfBorrowings() - 1);
            userRepository.save(member);
        }
        borrowingRepository.save(borrowing);
    }
}
