package com.financialsystem.domain;

import java.util.List;

public class Client {
    private Long id;
    private String name;
    private String email;
    private List<Account> accounts;
    private List<Loan> loans;
}
