package com.noureddine.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

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
    private String isbn;
    private String cote;
    private int numberOfCopies;
    private int availableCopies;
    private Boolean available;
    private String coverUrl;
    private String department;
    @Column(length = 1000)
    private String description;
    private LocalDate addedDate;


    public Book() {
    }
    public Book(String title, String author, String publisher, String editionYear, String isbn) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.editionYear = editionYear;
        this.isbn = isbn;
    }

    public Book(String title, String author, String publisher, String editionYear, String isbn, String cote, int numberOfCopies,  int availableCopies, Boolean available, String coverUrl, String department, String description, LocalDate addedDate) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.editionYear = editionYear;
        this.isbn = isbn;
        this.cote = cote;
        this.numberOfCopies = numberOfCopies;
        this.availableCopies = availableCopies;
        this.available = available;
        this.coverUrl = coverUrl;
        this.department = department;
        this.description = description;
        this.addedDate = addedDate;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
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

    public String getCote() {
        return cote;
    }

    public void setCote(String cote) {
        this.cote = cote;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}
