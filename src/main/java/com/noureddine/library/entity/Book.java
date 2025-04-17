package com.noureddine.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String Title;
    private String Author;
    private String Publisher;
    private String EditionYear;
    private Boolean available;

    public Book(String title, String author, String publisher, String editionYear, Boolean available) {
        Title = title;
        Author = author;
        Publisher = publisher;
        EditionYear = editionYear;
        this.available = available;
    }

    public Book() {
    }

    public Boolean isAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
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
}
