package com.noureddine.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Borrowing")
public class Borrowing {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "inventory_number", nullable = false)
    private BookCopy bookCopy;
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;
    private LocalDate pickUpDate;
    private LocalDate returnDate;

    public Borrowing() {
    }

    public Borrowing(BookCopy bookCopy, User member, LocalDate pickUpDate, LocalDate returnDate) {
        this.bookCopy = bookCopy;
        this.member = member;
        this.pickUpDate = pickUpDate;
        this.returnDate = returnDate;
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
}
