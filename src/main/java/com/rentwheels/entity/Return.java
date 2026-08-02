package com.rentwheels.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "returns")
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(optional = false)
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;

    @Column(nullable = false)
    private String returnDate;

    @Column(nullable = false)
    private int lateDays;

    @Column(nullable = false)
    private double fine;

    @Column(nullable = false)
    private double totalBill;

    @Column(unique = true, nullable = false)
    private String receiptNumber;

    public Return() {
    }

    public Return(int id, Rental rental, String returnDate, int lateDays, double fine, double totalBill, String receiptNumber) {
        this.id = id;
        this.rental = rental;
        this.returnDate = returnDate;
        this.lateDays = lateDays;
        this.fine = fine;
        this.totalBill = totalBill;
        this.receiptNumber = receiptNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public int getLateDays() {
        return lateDays;
    }

    public void setLateDays(int lateDays) {
        this.lateDays = lateDays;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
}
