package com.library.clientui.beans;

import java.util.Date;

public class ReservationBean {
    private int id;

    private int bookId;

    private int userId;

    private int priority;

    private Date deadline;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "ReservationBean{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", priority=" + priority +
                ", deadline=" + deadline +
                '}';
    }
}
