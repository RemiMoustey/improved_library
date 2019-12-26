package com.library.mloans.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Loan {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer userId;

    private Integer bookId;

    private Date deadline;

    private Boolean isExtended;

    public Loan() {

    }

    public Loan(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Boolean getExtended() {
        return isExtended;
    }

    public void setExtended(Boolean extended) {
        isExtended = extended;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", deadline=" + deadline +
                ", isExtended=" + isExtended +
                '}';
    }
}
