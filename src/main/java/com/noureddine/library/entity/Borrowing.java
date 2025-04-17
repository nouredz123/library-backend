package com.noureddine.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Borrowing")
public class Borrowing {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String inventoryNumber;
    private Long memberId;
    private LocalDate pickUpDate;
    private LocalDate returnDate;

    public Borrowing(String inventoryNumber, Long memberId, LocalDate pickUpDate, LocalDate returnDate) {
        this.inventoryNumber = inventoryNumber;
        this.memberId = memberId;
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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
