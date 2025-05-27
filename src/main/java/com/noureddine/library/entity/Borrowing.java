package com.noureddine.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Borrowing")
public class Borrowing {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "inventory_number", nullable = true)
    private BookCopy bookCopy;
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;
    private LocalDate addedDate;
    private LocalDate pickUpDate;
    private LocalDate returnDate;
    private String status;

    public Borrowing() {
    }

    public Borrowing(BookCopy bookCopy, User member, LocalDate addedDate, LocalDate pickUpDate, LocalDate returnDate, String status) {
        this.bookCopy = bookCopy;
        this.member = member;
        this.addedDate = addedDate;
        this.pickUpDate = pickUpDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(LocalDate pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
