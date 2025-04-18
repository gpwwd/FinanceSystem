package com.financialsystem.mapper;

import com.financialsystem.domain.model.Loan;
import com.financialsystem.dto.database.loan.PendingLoanDatabaseDto;
import com.financialsystem.dto.response.LoanResponseDto;
import com.financialsystem.dto.response.PendingLoanResponseDto;

public class LoanMapper {
    public static PendingLoanResponseDto toPendingLoanResponseDto(PendingLoanDatabaseDto loan) {
        return new PendingLoanResponseDto(loan.getId(), loan.getAccountId(), loan.getPrincipalAmount(), loan.getTermMonths(),
                loan.getRequestStatus(), loan.isFixedInterest());
    }
}
