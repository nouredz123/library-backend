package com.noureddine.library.service;

import com.noureddine.library.dto.InsightsResponse;
import com.noureddine.library.repository.BookCopyRepository;
import com.noureddine.library.repository.BookRepository;
import com.noureddine.library.repository.BorrowingRepository;
import com.noureddine.library.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InsightsService {
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;

    public InsightsService(BookRepository bookRepository, BorrowingRepository borrowingRepository, UserRepository userRepository, BookCopyRepository bookCopyRepository) {
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    public InsightsResponse getInsights(){
        Long totalBooks = bookRepository.count();
        Long totalBorrowings = borrowingRepository.count();
        Long activeBorrowings = borrowingRepository.countByStatus("PICKED_UP");
        Long availableBooks = bookCopyRepository.countByAvailable(true);
        Long pendingAccountRequests = userRepository.countByAccountStatus("PENDING");
        Long activeUsers =  userRepository.countByLastActiveDateAfterAndAccountStatusAndRole(LocalDate.now().minusDays(30), "APPROVED","ROLE_MEMBER");
        Long totalUsers = userRepository.count();
        return new InsightsResponse(totalBooks, totalBorrowings, totalUsers, activeBorrowings, activeUsers, availableBooks, pendingAccountRequests);
    }
}
