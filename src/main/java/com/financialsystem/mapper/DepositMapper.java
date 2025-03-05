package com.financialsystem.mapper;

import com.financialsystem.domain.model.Deposit;
import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.response.DepositResponseDto;
import com.financialsystem.dto.response.PendingClientResponseDto;

public class DepositMapper {
    public static DepositResponseDto toDepositResponseDto(Deposit deposit) {
        var d = deposit.toDto();
        return new DepositResponseDto(d.getId(), d.getPrincipalBalance(), d.getBalance(), d.getAccountId(),
                d.getDepositStatus(), d.getInterestRate(), d.getCreatedAt(), d.getLastInterestDate(), d.getTermMonths());
    }
}
