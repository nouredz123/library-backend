package com.noureddine.library.dto;


import java.util.List;

public class BookRequest {
    private String Title;
    private String Author;
    private String Publisher;
    private String EditionYear;
    private List<String> inventoryNumbers;

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

    public List<String> getInventoryNumbers() {
        return inventoryNumbers;
    }

    public void setInventoryNumbers(List<String> inventoryNumbers) {
        this.inventoryNumbers = inventoryNumbers;
    }
}
