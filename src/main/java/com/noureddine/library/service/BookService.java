package com.noureddine.library.service;

import com.noureddine.library.dto.PageResponse;
import com.noureddine.library.entity.Book;
import com.noureddine.library.entity.BookCopy;
import com.noureddine.library.dto.BookRequest;
import com.noureddine.library.entity.Borrowing;
import com.noureddine.library.entity.User;
import com.noureddine.library.exception.InvalidArgumentException;
import com.noureddine.library.exception.NotFoundException;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import com.noureddine.library.repository.BorrowingRepository;
import com.noureddine.library.repository.UserRepository;
import com.noureddine.library.utils.SearchUtils;
import com.noureddine.library.utils.SortUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowingRepository borrowingRepository;
    private final UserRepository  userRepository;

    public BookService(BookRepository bookRepository, BookCopyRepository bookCopyRepository, BorrowingRepository borrowingRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
    }

    public PageResponse<Book> getBooks(String department, int page, int size, String sortBy, String direction) throws NotFoundException {
        List<Book> books;
        //check if the status is not null nor empty, if it is get only the books with that status else get all the books
        if (department != null && !department.isEmpty()){
            books = bookRepository.findByDepartment(department.toUpperCase());
        }else{
            books = bookRepository.findAll();
        }
        //check if the there are no books on the database throw an exception
        if(books.isEmpty()){
            throw new NotFoundException("There are no books");
        }
        //calculate the number of all elements and the total number of pages
        int totalElements = books.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        //check if the page number is not valid then throw an exception
        if (page < 0 || page >= totalPages) {
            throw new IllegalArgumentException("Page number out of range.");
        }
        //decide which order to follow based on the direction argument
        boolean descending = direction.equalsIgnoreCase("desc");
        //sort the books list by the sortBy argument and throw an exception if the sortBy is not valid
        switch (sortBy.toLowerCase()) {
            case "id":
                SortUtils.heapSort(books, Book::getId, descending);
                break;
            case "department":
                SortUtils.heapSort(books, Book::getDepartment, descending);
                break;
            case "addeddate":
                SortUtils.heapSort(books, Book::getAddedDate, descending);
                break;
            case "title":
                SortUtils.heapSort(books, Book::getTitle, descending);
                break;
            case "author":
                SortUtils.heapSort(books, Book::getAuthor, descending);
                break;
            case "editionyear":
                SortUtils.heapSort(books, Book::getEditionYear, descending);
                break;
            case "numberofcopies":
                SortUtils.heapSort(books, Book::getNumberOfCopies, descending);
                break;
            case "isbn":
                SortUtils.heapSort(books, Book::getIsbn, descending);
                break;
            case "cote":
                SortUtils.heapSort(books, Book::getCote, descending);
                break;
            case "publisher":
                SortUtils.heapSort(books, Book::getPublisher, descending);
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy field: " + sortBy);
        }
        for(int i = 0; i <books.size(); i++ ){
            System.out.println(books.get(i).getTitle());
        }
        //create a pageResponse instance and set its attributes
        PageResponse<Book> booksPage = new PageResponse<>();
        booksPage.setTotalElements(totalElements);
        booksPage.setTotalPages(totalPages);
        booksPage.setPageSize(size);
        booksPage.setPageNumber(page);
        booksPage.setFirst(page == 0);
        booksPage.setLast(page >= totalPages - 1);
        //fill the content based on which page is requested
        List<Book> content = new ArrayList<>();
        //the index of first element of the page
        int start = size * page;
        //the index of last element of the page
        int end = Math.min(start + size, books.size());
        for (int i = start; i < end; i++) {
            content.add(books.get(i));
        }
        booksPage.setContent(content);
        return booksPage;
    }
    public PageResponse<Book> searchBooks(String keyword, Boolean available, int page, int size, String sortBy, String direction) throws NotFoundException {
        List<Book> books;
        if (available != null){
           books = bookRepository.searchBooksAndAvailable(keyword, available);
        }else{
           books = bookRepository.searchBooks(keyword);
        }
        //check if the there are no books on the database then throw an exception
        if(books.isEmpty()){
            throw new NotFoundException("There are no books");
        }
        //calculate the number of all elements and the total number of pages
        int totalElements = books.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        //check if the page number is not valid then throw an exception
        if (page < 0 || page >= totalPages) {
            throw new IllegalArgumentException("Page number out of range.");
        }
        //decide which order to follow based on the direction argument
        boolean descending = direction.equalsIgnoreCase("desc");
        //sort the books list by the sortBy argument and throw an exception if the sortBy is not valid
        switch (sortBy.toLowerCase()) {
            case "id":
                SortUtils.heapSort(books, Book::getId, descending);
                break;
            case "department":
                SortUtils.heapSort(books, Book::getDepartment, descending);
                break;
            case "addeddate":
                SortUtils.heapSort(books, Book::getAddedDate, descending);
                break;
            case "title":
                SortUtils.heapSort(books, Book::getTitle, descending);
                break;
            case "author":
                SortUtils.heapSort(books, Book::getAuthor, descending);
                break;
            case "editionyear":
                SortUtils.heapSort(books, Book::getEditionYear, descending);
                break;
            case "numberofcopies":
                SortUtils.heapSort(books, Book::getNumberOfCopies, descending);
                break;
            case "isbn":
                SortUtils.heapSort(books, Book::getIsbn, descending);
                break;
            case "cote":
                SortUtils.heapSort(books, Book::getCote, descending);
                break;
            case "publisher":
                SortUtils.heapSort(books, Book::getPublisher, descending);
                break;
            default:
                throw new IllegalArgumentException("Invalid sortBy field: " + sortBy);
        }
        //create a pageResponse instance and set its attributes
        PageResponse<Book> booksPage = new PageResponse<>();
        booksPage.setTotalElements(totalElements);
        booksPage.setTotalPages(totalPages);
        booksPage.setPageSize(size);
        booksPage.setPageNumber(page);
        booksPage.setFirst(page == 0);
        booksPage.setLast(page >= totalPages - 1);
        //fill the content based on which page is requested
        List<Book> content = new ArrayList<>();
        //the index of first element of the page
        int start = size * page;
        //the index of last element of the page
        int end = Math.min(start + size, books.size());
        for (int i = start; i < end; i++) {
            content.add(books.get(i));
        }
        booksPage.setContent(content);
        return booksPage;
    }
    public void addBook(BookRequest bookRequest) throws NotFoundException {
        //check for the isbn ti make sure it is valid and does not exist before
        if(!isIsbnValid(bookRequest.getIsbn())){
            throw new NotFoundException("The isbn provided is not valid. Properly book with that isbn already in the database");
        }
        //check for the isbn ti make sure it is valid and does not exist before
        if(!isCoteValid(bookRequest.getCote())){
            throw new NotFoundException("The cote provided is not valid. Properly book with that cote already in the database");
        }
        //create and initialize a book instance with the infos provided
        Book book = new Book(
                bookRequest.getTitle(),
                bookRequest.getAuthor(),
                bookRequest.getPublisher(),
                bookRequest.getEditionYear(),
                bookRequest.getIsbn(),
                bookRequest.getCote(),
                bookRequest.getNumberOfCopies(),
                bookRequest.getNumberOfCopies(),
                true,
                bookRequest.getCoverUrl(),
                bookRequest.getDepartment(),
                bookRequest.getDescription(),
                LocalDate.now()
        );
        //save the book at the database
        Book savedBook = bookRepository.save(book);
        //create book copies based on the number provided and save them on the bookCopy table on the database
        for (int i = 1; i <= bookRequest.getNumberOfCopies(); i++){
            BookCopy copy = new BookCopy(
                    bookRequest.getCote() + "." + i,
                    savedBook,
                    true
            );
            bookCopyRepository.save(copy);
        }
    }
    public void removeBook(Long bookId) throws NotFoundException {
        //get all the books from the database and throw an exception if there are no books found
        List<Book> books = bookRepository.findAll();
        if(books.isEmpty()){
            throw new NotFoundException("There are no books on the database");
        }
        //sort the books list and search for the book we want to remove
        SortUtils.heapSort(books, Book::getId);
        Book book = SearchUtils.binarySearch(books, bookId, Book::getId);
        if (book == null){
            throw new NotFoundException("The book not found");
        }
        List<Borrowing> borrowingList = borrowingRepository.findByBook(book);
        List<Borrowing> toDelete = new ArrayList<>();
        if(!borrowingList.isEmpty()){
            for(Borrowing borrowing: borrowingList){
                switch (borrowing.getStatus().toUpperCase()) {
                    case "PENDING":
                        toDelete.add(borrowing); // Mark for deletion
                        User member = borrowing.getMember();
                        member.setNumberOfBorrowings(member.getNumberOfBorrowings() - 1);
                        userRepository.save(member);
                        break;
                    case "RETURNED":
                        toDelete.add(borrowing); // Mark for deletion
                        break;
                    case "PICKED_UP":
                        throw new InvalidArgumentException("The book has borrowed copies that are not yet returned.");
                }
            }
            borrowingRepository.saveAll(borrowingList);
            if (!toDelete.isEmpty()) {
                borrowingRepository.deleteAll(toDelete);
            }
        }
        //get the list of all the copies of that book and remove them
        List<BookCopy> bookCopies = bookCopyRepository.findByBook(book);
        if(!bookCopies.isEmpty()){
            for(BookCopy copy : bookCopies){
                bookCopyRepository.deleteById(copy.getInventoryNumber());
            }
        }
        bookRepository.deleteById(book.getId());
    }

    public boolean isCoteValid(String cote){
        Boolean isBookExists = bookRepository.existsByCote(cote);
        return !isBookExists;
    }

    public boolean isIsbnValid(String isbn) {
        Boolean isBookExists = bookRepository.existsByIsbn(isbn);
        return !isBookExists;
    }
}
