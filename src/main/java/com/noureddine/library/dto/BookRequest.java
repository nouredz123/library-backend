package com.noureddine.library.dto;


import java.util.List;

public class BookRequest {
    private String Title;
    private String Author;
    private String Publisher;
    private String EditionYear;
    private String isbn;
    private int numberOfCopies;
    private String coverUrl;
    private String department;
    private List<String> inventoryNumbers;

    public BookRequest() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getEditionYear() {
        return EditionYear;
    }

    public void setEditionYear(String editionYear) {
        EditionYear = editionYear;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<String> getInventoryNumbers() {
        return inventoryNumbers;
    }

    public void setInventoryNumbers(List<String> inventoryNumbers) {
        this.inventoryNumbers = inventoryNumbers;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getNumberOfCopies() {
        return numberOfCopies;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }
}
