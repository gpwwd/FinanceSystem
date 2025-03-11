package com.financialsystem.dto.request;

import java.util.List;

public record SalaryProjectRequest(String currency, List<String> employeesPassports) {}

