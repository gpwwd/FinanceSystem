package com.financialsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class FinancialSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialSystemApplication.class, args);
	}

}
