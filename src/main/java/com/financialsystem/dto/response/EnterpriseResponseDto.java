package com.financialsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EnterpriseResponseDto {
    private Long id;
    private String type;
    private String legalName;
    private String unp;
    private Long bankId;
    private Long payrollAccountId;
    private String legalAddress;
    private LocalDateTime createdAt;
}
