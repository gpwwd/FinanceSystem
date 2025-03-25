package com.financialsystem.dto.request;

import com.financialsystem.domain.model.Currency;

public record EnterpriseAccountRegistrationRequest(
        Currency currency
) {
}
