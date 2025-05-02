package com.noureddine.library.dto;

public class InsightsResponse {
    private Long totalBooks;
    private Long totalBorrowings;
    private Long totalUsers;
    private Long activeBorrowings;
    private Long activeUsers;
    private Long availableBooks;
    private Long pendingAccountRequests;

    public InsightsResponse(Long totalBooks, Long totalBorrowings, Long totalUsers, Long activeBorrowings, Long activeUsers, Long availableBooks, Long pendingAccountRequests) {
        this.totalBooks = totalBooks;
        this.totalBorrowings = totalBorrowings;
        this.totalUsers = totalUsers;
        this.activeBorrowings = activeBorrowings;
        this.activeUsers = activeUsers;
        this.availableBooks = availableBooks;
        this.pendingAccountRequests = pendingAccountRequests;
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

    public Long getActiveBorrowings() {
        return activeBorrowings;
    }

    public void setActiveBorrowings(Long activeBorrowings) {
        this.activeBorrowings = activeBorrowings;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Long getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(Long availableBooks) {
        this.availableBooks = availableBooks;
    }

    public Long getPendingAccountRequests() {
        return pendingAccountRequests;
    }

    public void setPendingAccountRequests(Long pendingAccountRequests) {
        this.pendingAccountRequests = pendingAccountRequests;
    }
}
