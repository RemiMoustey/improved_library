package com.library.mbooks.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("configs")
@RefreshScope
public class ApplicationPropertiesConfiguration {

    private int limitPrintedBooks;

    public int getLimitPrintedBooks() {
        return limitPrintedBooks;
    }

    public void setLimitPrintedBooks(int limitPrintedBooks) {
        this.limitPrintedBooks = limitPrintedBooks;
    }
}
