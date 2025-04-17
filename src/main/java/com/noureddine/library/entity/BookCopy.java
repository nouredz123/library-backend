package com.noureddine.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookCopy")
public class BookCopy {
    @Id
    private String inventoryNumber;
    private Long bookId;
    private boolean available;

    public BookCopy(String inventoryNumber, Long bookId, boolean available) {
        this.inventoryNumber = inventoryNumber;
        this.bookId = bookId;
        this.available = available;
    }

    public BookCopy() {
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
