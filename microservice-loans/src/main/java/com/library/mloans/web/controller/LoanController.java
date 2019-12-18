package com.library.mloans.web.controller;

import com.library.mloans.dao.LoanDao;
import com.library.mloans.exceptions.LoanNotFoundException;
import com.library.mloans.model.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class LoanController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LoanDao loanDao;

}
