package com.library.clientui.beans;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Table(name = "loanBean")
public class LoanBean {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    private int userId;

    private Date deadline;

    private boolean isExtended;

    private int bookId;

    public LoanBean() {

    }

    public LoanBean(int bookId) {
        this.bookId = bookId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isExtended() {
        return isExtended;
    }

    public void setExtended(boolean extended) {
        isExtended = extended;
    }

    @Override
    public String toString() {
        return "LoanBean{" +
                "id=" + id +
                ", userId=" + userId +
                ", deadline=" + deadline +
                ", isExtended=" + isExtended +
                ", bookId=" + bookId +
                '}';
    }
}
