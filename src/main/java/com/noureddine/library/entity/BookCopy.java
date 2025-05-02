package com.noureddine.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bookCopy")
public class BookCopy {
    @Id
    private String inventoryNumber;
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    private boolean available;
    private String status;

    public BookCopy(String inventoryNumber, Book book, boolean available, String status) {
        this.inventoryNumber = inventoryNumber;
        this.book = book;
        this.available = available;
        this.status = status;
    }

    public BookCopy() {
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
