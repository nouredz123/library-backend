package com.noureddine.library.dto;

import com.noureddine.library.entity.Book;

import java.util.List;

public class CopiesResponse {
    private Book book;
    private List<String> inventoryNumbers;

    public CopiesResponse(Book book, List<String> inventoryNumbers) {
        this.book = book;
        this.inventoryNumbers = inventoryNumbers;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<String> getInventoryNumbers() {
        return inventoryNumbers;
    }

    public void setInventoryNumbers(List<String> inventoryNumbers) {
        this.inventoryNumbers = inventoryNumbers;
    }

    public CopiesResponse() {
    }
}
