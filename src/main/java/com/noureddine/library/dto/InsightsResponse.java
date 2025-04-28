package com.noureddine.library.dto;

public class InsightsResponse {
    private Long totalBooks;
    private Long totalBorrowings;
    private Long totalUsers;

    public InsightsResponse(Long totalBooks, Long totalBorrowings, Long totalUsers) {
        this.totalBooks = totalBooks;
        this.totalBorrowings = totalBorrowings;
        this.totalUsers = totalUsers;
    }

    public Long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(Long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public Long getTotalBorrowings() {
        return totalBorrowings;
    }

    public void setTotalBorrowings(Long totalBorrowings) {
        this.totalBorrowings = totalBorrowings;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }
}
