package com.noureddine.library.service;

import com.noureddine.library.dto.InsightsResponse;
import com.noureddine.library.repository.BookRepository;
import com.noureddine.library.repository.BorrowingRepository;
import com.noureddine.library.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportsService {
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;

    public ReportsService(BookRepository bookRepository, BorrowingRepository borrowingRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.borrowingRepository = borrowingRepository;
        this.userRepository = userRepository;
    }

    public InsightsResponse getInsights(){
        Long totalBooks = bookRepository.count();
        Long totalBorrowings = borrowingRepository.count();
        Long totalUsers = userRepository.count();
        return new InsightsResponse(totalBooks, totalBorrowings, totalUsers);
    }
}
