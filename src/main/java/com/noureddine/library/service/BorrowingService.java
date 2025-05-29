package com.noureddine.library.service;

import com.noureddine.library.dto.BorrowRequest;
import com.noureddine.library.dto.PageResponse;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.InvalidArgumentException;
import com.noureddine.library.exception.InvalidDataException;
import com.noureddine.library.exception.MaxBorrowLimitException;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import com.noureddine.library.repository.BorrowingRepository;
import com.noureddine.library.repository.UserRepository;
import com.noureddine.library.utils.SearchUtils;
import com.noureddine.library.utils.SortUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        //cancel pending borrowings if they don't picked_up within 3 days
        cancelOverduePendingBorrowings();
        System.out.println("cancel overdue pending borrowings ran at: " + LocalDate.now());
        //mark the borrowing as overdue if the return date is reach and the book is not returned yet
        OverduePickedUpBorrowings();
        System.out.println("Overdue pickedUp borrowings ran at: " + LocalDate.now());
    }

    public void cancelOverduePendingBorrowings() {
        //get all the pending borrowings
        List<Borrowing> pendingBorrowings = borrowingRepository.findByStatus("PENDING");

        for (Borrowing borrowing : pendingBorrowings) {
            // If pick up date is set, and it is in the past, cancel it
            if (borrowing.getPickUpDate() != null && borrowing.getPickUpDate().isBefore(LocalDate.now())) {
                borrowing.setStatus("CANCELLED");
                borrowingRepository.save(borrowing);

                //make the book copy available again
                BookCopy bookCopy = borrowing.getBookCopy();
                bookCopy.setAvailable(true);
                bookCopyRepository.save(bookCopy);

                //update the number of available copies for the book
                Book book = borrowing.getBookCopy().getBook();
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                if(!book.isAvailable()){
                    book.setAvailable(true);
                }
                bookRepository.save(book);

                //update number of borrowings for the user
                User member = borrowing.getMember();
                member.setNumberOfBorrowings(member.getNumberOfBorrowings() - 1);
                userRepository.save(member);
            }
        }
    }

    public void OverduePickedUpBorrowings() {
        //get all the picked_up borrowings
        List<Borrowing> picked_upBorrowings = borrowingRepository.findByStatus("PICKED_UP");
        //for each picked_up borrowing if the return date is in the past mark it as overdue
        for (Borrowing borrowing : picked_upBorrowings) {
            if (borrowing.getPickUpDate() != null && borrowing.getPickUpDate().isBefore(LocalDate.now())) {
                borrowing.setStatus("OVERDUE");
                borrowingRepository.save(borrowing);
            }
        }
    }

    public PageResponse<Borrowing> getAll(String keyword, String status, int page, int size, String sortBy, String direction) {
        List<Borrowing> borrowings;
        //check for the keyword if it is null then get borrowings if it is not null then search for borrowings with that keyword
        if(keyword == null || keyword.isBlank()){
            //check if the status is not null nor empty, if it is get only the borrowings with that status else get all the borrowings
            if (status != null && !status.isEmpty()){
                if(List.of("PENDING", "PICKED_UP", "RETURNED", "OVERDUE").contains(status.toUpperCase())){
                    borrowings = borrowingRepository.findByStatus(status.toUpperCase());
                }else{
                    throw new InvalidArgumentException("Invalid status. Must be 'PENDING' or 'PICKED_UP' or 'RETURNED or 'OVERDUE'.");
                }
            }else{
                borrowings = borrowingRepository.findAll();
            }
        }else{
            //check if the status is not null nor empty, if it is get only the borrowings with that status else get all the borrowings
            if (status != null && !status.isEmpty()){
                if(List.of("PENDING", "PICKED_UP", "RETURNED", "OVERDUE").contains(status.toUpperCase())){
                    borrowings = borrowingRepository.searchBorrowingsAndStatus(keyword, status.toUpperCase());
                }else{
                    throw new InvalidArgumentException("Invalid status. Must be 'PENDING' or 'PICKED_UP' or 'RETURNED or 'OVERDUE'.");
                }
            }else{
                borrowings = borrowingRepository.searchBorrowings(keyword);
            }
        }

        //check if the there are no borrowings on the database throw an exception
        if(borrowings.isEmpty()){
            throw new NotFoundException("There are no borrowings");
        }
        //calculate the number of all elements and the total number of pages
        int totalElements = borrowings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        //check if the page number is not valid then throw an exception
        if (page < 0 || page >= totalPages) {
            throw new InvalidDataException("Page number out of range.");
        }
        //decide which order to follow based on the direction argument
        boolean descending = direction.equalsIgnoreCase("desc");
        //sort the borrowings list by the sortBy argument and throw an exception if the sortBy is not valid
        switch (sortBy.toLowerCase()) {
            case "id":
                SortUtils.heapSort(borrowings, Borrowing::getId, descending);
                break;
            case "status":
                SortUtils.mergeSort(borrowings, Borrowing::getStatus, descending);
                break;
            case "addeddate":
                SortUtils.mergeSort(borrowings, Borrowing::getAddedDate, descending);
                break;
            case "pickupdate":
                SortUtils.mergeSort(borrowings, Borrowing::getPickUpDate, descending);
                break;
            case "returndate":
                SortUtils.mergeSort(borrowings, Borrowing::getReturnDate, descending);
                break;
            default:
                throw new InvalidArgumentException("Invalid sortBy field: " + sortBy);
        }
        //create a pageResponse instance and set its attributes
        PageResponse<Borrowing> borrowingsPage = new PageResponse<>();
        borrowingsPage.setTotalElements(totalElements);
        borrowingsPage.setTotalPages(totalPages);
        borrowingsPage.setPageSize(size);
        borrowingsPage.setPageNumber(page);
        borrowingsPage.setFirst(page == 0);
        borrowingsPage.setLast(page >= totalPages - 1);
        //fill the content based on which page is requested
        List<Borrowing> content = new ArrayList<>();
        //get index of first element of the requested page
        int start = size * page;
        //get index of last element of the requested page
        int end = Math.min(start + size, borrowings.size());
        //fill in the content with based on the requested page
        for (int i = start; i < end; i++) {
            content.add(borrowings.get(i));
        }
        borrowingsPage.setContent(content);
        return borrowingsPage;
    }

    public PageResponse<Borrowing> getBorrowingsByMemberId(Long memberId, String status, int page, int size, String sortBy, String direction) {
        //seach for the user with the provided memberId
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            throw new NotFoundException("There are no users on the database");
        }
        SortUtils.heapSort(users, User::getId);
        User member = SearchUtils.binarySearch(users, memberId, User::getId);
        if(member == null){
            throw new NotFoundException("Member not found");
        }

        List<Borrowing> borrowings;
        //check if the status is not null nor empty, if it is get only the borrowings with that status else get all the borrowings
        if (status != null && !status.isEmpty()){
            if (status.equalsIgnoreCase("PENDING")
                    || status.equalsIgnoreCase("ACTIVE")
                    || status.equalsIgnoreCase("RETURNED")) {
                if(status.equalsIgnoreCase("ACTIVE")){
                    borrowings = borrowingRepository.findByMemberAndStatusIn(member, List.of("PICKED_UP", "OVERDUE"));
                }else{
                    borrowings = borrowingRepository.findByMemberAndStatus(member, status.toUpperCase());
                }
            }else {
                throw new InvalidArgumentException("Invalid status. Must be 'PENDING' or 'ACTIVE' or 'RETURNED.");
            }
        }else{
            borrowings = borrowingRepository.findByMember(member);
        }
        //check if the there are no borrowings on the database throw an exception
        if(borrowings.isEmpty()){
            throw new NotFoundException("There are no borrowings");
        }
        //calculate the number of all elements and the total number of pages
        int totalElements = borrowings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        //check if the page number is not valid then throw an exception
        if (page < 0 || page >= totalPages) {
            throw new InvalidArgumentException("Page number out of range.");
        }
        //decide which order to follow based on the direction argument
        boolean descending = direction.equalsIgnoreCase("desc");
        //sort the borrowings list by the sortBy argument and throw an exception if the sortBy is not valid
        switch (sortBy.toLowerCase()) {
            case "id":
                SortUtils.heapSort(borrowings, Borrowing::getId, descending);
                break;
            case "status":
                SortUtils.heapSort(borrowings, Borrowing::getStatus, descending);
                break;
            case "addeddate":
                SortUtils.heapSort(borrowings, Borrowing::getAddedDate, descending);
                break;
            case "pickupdate":
                SortUtils.heapSort(borrowings, Borrowing::getPickUpDate, descending);
                break;
            case "returndate":
                SortUtils.heapSort(borrowings, Borrowing::getReturnDate, descending);
                break;
            default:
                throw new InvalidDataException("Invalid sortBy field: " + sortBy);
        }
        //create a pageResponse instance and set its attributes
        PageResponse<Borrowing> borrowingsPage = new PageResponse<>();
        borrowingsPage.setTotalElements(totalElements);
        borrowingsPage.setTotalPages(totalPages);
        borrowingsPage.setPageSize(size);
        borrowingsPage.setPageNumber(page);
        borrowingsPage.setFirst(page == 0);
        borrowingsPage.setLast(page >= totalPages - 1);
        //fill the content based on which page is requested
        List<Borrowing> content = new ArrayList<>();
        //get index of first element of the requested page
        int start = size * page;
        //get index of last element of the requested page
        int end = Math.min(start + size, borrowings.size());
        //fill in the content with based on the requested page
        for (int i = start; i < end; i++) {
            content.add(borrowings.get(i));
        }
        borrowingsPage.setContent(content);
        return borrowingsPage;
    }

    public void borrow(BorrowRequest request) {
        //find the book on the database and throw an exception if it's not found
        List<Book> books = bookRepository.findAll();
        if(books.isEmpty()){
            throw new NotFoundException("No books found in the database");
        }
        //sort the list of the books and then search for the id using the binarySearch method
        SortUtils.heapSort(books, Book::getId);
        Book book = SearchUtils.binarySearch(books, request.getBookId(), Book::getId);
        if(book == null){
            throw new NotFoundException("The book not found");
        }
        //find the member on the database and throw an exception if it's not found
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            throw new NotFoundException("No users found in the database");
        }
        SortUtils.heapSort(users, User::getId);
        User member = SearchUtils.binarySearch(users, request.getMemberId(), User::getId);
        if(member == null){
            throw new NotFoundException("There is no user with the provided id");
        }
        //make sure the user account is approved
        if(!member.getAccountStatus().equalsIgnoreCase("APPROVED")){
            throw new InvalidArgumentException("Your account is pending approval. Please wait for confirmation.");
        }
        //make sure the user does not reach the maximum allowed number of borrowings
        if(member.getNumberOfBorrowings() >= 2){
            throw new MaxBorrowLimitException("You have reached the maximum number of borrowings");
        }
        //check if the book has any available copies
        if (!book.isAvailable() || book.getAvailableCopies() == 0) {
            throw new NotFoundException("There are no available copies for this book");
        }
        //check if the user already has the book borrowed (pending or picked up)
        boolean alreadyBorrowed = borrowingRepository.existsByBookCopy_BookAndMemberAndStatusIn(
                book,
                member,
                List.of("PENDING", "PICKED_UP")
        );
        if (alreadyBorrowed) {
            throw new NotFoundException("Book already borrowed by this user and not yet returned.");
        }
        //get the first available book copy
        BookCopy copy = bookCopyRepository.findFirstByBookAndAvailable(book, true)
                .orElseThrow(() -> new NotFoundException("There are no available copies for this book"));
        //create a new borrowing with pending status and 3 days to pickup and 1 week to return after pickup
        Borrowing borrowing = new Borrowing(
                copy,
                member,
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(10),
                "PENDING"
        );
        //save the borrowing
        borrowingRepository.save(borrowing);
        //mark the book copy as unavailable
        copy.setAvailable(false);
        bookCopyRepository.save(copy);
        //get the available copies and check if there are no available copies, then mark the book as unavailable
        if (bookCopyRepository.findByBookAndAvailable(book, true).isEmpty()) {
            book.setAvailable(false);
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if(book.getAvailableCopies() == 0){
            book.setAvailable(false);
        }
        bookRepository.save(book);
        //update the number of the borrowed books by this user
        member.setNumberOfBorrowings(member.getNumberOfBorrowings() + 1);
        userRepository.save(member);
    }

    public void removeBorrowingById(Long borrowingId) {
        //find the borrowing on the database and throw an exception if it's not found
        List<Borrowing> borrowings = borrowingRepository.findAll();
        if(borrowings.isEmpty()){
            throw new NotFoundException("No borrowings found in the database");
        }
        //sort the list of the borrowings and then search for the id using the binarySearch method
        SortUtils.heapSort(borrowings, Borrowing::getId);
        Borrowing borrowing = SearchUtils.binarySearch(borrowings, borrowingId, Borrowing::getId);
        if(borrowing == null){
            throw new NotFoundException("The borrowing not found");
        }
        //delete the borrowing after making sure that it exists
        borrowingRepository.deleteById(borrowingId);
        //find the bookCopy and throw an exception if it's not found
        BookCopy bookCopy = bookCopyRepository.findById(borrowing.getBookCopy().getInventoryNumber())
                .orElseThrow(()-> new NotFoundException("The book copy not found"));
        //make the bookCopy available again
        bookCopy.setAvailable(true);
        bookCopyRepository.save(bookCopy);
        //make the book itself available if it is not
        Book book = bookCopy.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        if(!book.isAvailable()){
            book.setAvailable(true);
        }
        bookRepository.save(book);
        //update the number of borrowings for the member
        User member = borrowing.getMember();
        member.setNumberOfBorrowings(member.getNumberOfBorrowings() - 1);
        userRepository.save(member);
    }

    public void reviewBorrowing(Long borrowingId, String status) {
        //find the borrowing on the database and throw an exception if it's not found
        List<Borrowing> borrowings = borrowingRepository.findAll();
        if(borrowings.isEmpty()){
            throw new NotFoundException("No borrowings found in the database");
        }
        //sort the list of the borrowings and then search for the id using the binarySearch method
        SortUtils.heapSort(borrowings, Borrowing::getId);
        Borrowing borrowing = SearchUtils.binarySearch(borrowings, borrowingId, Borrowing::getId);
        if(borrowing == null){
            throw new NotFoundException("The borrowing not found");
        }
        //check if status is valid or not, if it is not throw an exception
        if(status == null || status.isBlank() || !(status.equalsIgnoreCase("PENDING") || status.equalsIgnoreCase("PICKED_UP") || status.equalsIgnoreCase("RETURNED"))){
            throw new NotFoundException("Invalid status");
        }

        if(status.equalsIgnoreCase("PICKED_UP")){
            //if the book picked_up, update the pickup date and the return date to be one week after the pickup date
            borrowing.setPickUpDate(LocalDate.now());
            borrowing.setReturnDate(LocalDate.now().plusWeeks(1));
        } else if (status.equalsIgnoreCase("RETURNED")){
            //if the book returned, update the return date and update the number of borrowings for that user
            borrowing.setReturnDate(LocalDate.now());
            //find the user on the database and throw an exception if it's not found
            List<User> users = userRepository.findAll();
            if(users.isEmpty()){
                throw new NotFoundException("No users found in the database");
            }
            //sort the list of the borrowings and then search for the id using the binarySearch method
            SortUtils.heapSort(users, User::getId);
            User member = SearchUtils.binarySearch(users, borrowing.getMember().getId(), User::getId);
            if(member == null){
                throw new NotFoundException("The user not found");
            }
            //update the number of borrowings after making a return
            member.setNumberOfBorrowings(member.getNumberOfBorrowings() - 1);
            userRepository.save(member);
            //make the copy available
            BookCopy copy = borrowing.getBookCopy();
            copy.setAvailable(true);
            bookCopyRepository.save(copy);
            //update the number of available copies and the available status for the returned book
            List<Book> books = bookRepository.findAll();
            if(books.isEmpty()){
                throw new NotFoundException("No books found in the database");
            }
            //sort the list of the books and then search for the id using the binarySearch method
            SortUtils.heapSort(books, Book::getId);
            Book book = SearchUtils.binarySearch(books, borrowing.getBookCopy().getBook().getId(), Book::getId);
            if(book == null){
                throw new NotFoundException("The book not found");
            }
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            if(!book.isAvailable()){
                book.setAvailable(true);
            }
            bookRepository.save(book);
        }
        //change the status of the borrowing to the new status
        borrowing.setStatus(status.toUpperCase());
        //save all the updates on the borrowing
        borrowingRepository.save(borrowing);
    }

    public PageResponse<Borrowing> getAllBorrowingsByMemberId(Long memberId) {
        //search for the user
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            throw new NotFoundException("There are no users on the database");
        }
        SortUtils.heapSort(users, User::getId);
        User member = SearchUtils.binarySearch(users, memberId, User::getId);
        if(member == null){
            throw new NotFoundException("Member not found");
        }
        //get all the borrowings
        List<Borrowing> borrowings;
        borrowings = borrowingRepository.findByMember(member);
        //check if the there are no borrowings on the database throw an exception
        if(borrowings.isEmpty()){
            throw new NotFoundException("There are no borrowings");
        }
        //return a pageResponses as a one page
        return new PageResponse<>(
                borrowings,
                0,
                borrowings.size(),
                borrowings.size(),
                1,
                true
        );
    }
}