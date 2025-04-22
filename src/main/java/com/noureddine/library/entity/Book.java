package com.noureddine.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String title;
    private String author;
    private String publisher;
    private String editionYear;
    private Boolean available;
    private String coverUrl;


    public Book() {
    }

    public Book(String title, String author, String publisher, String editionYear, Boolean available, String coverUrl) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.editionYear = editionYear;
        this.available = available;
        this.coverUrl = coverUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Boolean getAvailable() {
        return available;
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEditionYear() {
        return editionYear;
    }

    public void setEditionYear(String editionYear) {
        this.editionYear = editionYear;
    }
}
