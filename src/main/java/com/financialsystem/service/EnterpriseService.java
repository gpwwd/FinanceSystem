package com.financialsystem.service;

import com.financialsystem.domain.model.Enterprise;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.domain.model.user.Specialist;
import com.financialsystem.dto.response.EnterpriseResponseDto;
import com.financialsystem.repository.EnterpriseRepository;
import com.financialsystem.repository.user.SpecialistRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final SpecialistRepository specialistRepository;
    private final EntityFinder entityFinder;

    public EnterpriseService(EnterpriseRepository enterpriseRepository, SpecialistRepository specialistRepository,
                             EntityFinder entityFinder) {
        this.enterpriseRepository = enterpriseRepository;
        this.specialistRepository = specialistRepository;
        this.entityFinder = entityFinder;
    }

    public EnterpriseResponseDto getEnterpriseById(BankingUserDetails userDetails) {
        Specialist specialist = entityFinder.findEntityById(userDetails.getId(), specialistRepository, "Специалист стороннего предприятия");
        return entityFinder.findEntityById(specialist.getEnterpriseId(), enterpriseRepository, "Предприятие").toResponseDto();
    }

    public EnterpriseResponseDto getEnterpriseById(Long enterpriseId) {
        return entityFinder.findEntityById(enterpriseId, enterpriseRepository, "Предприятие").toResponseDto();
    }

    public List<EnterpriseResponseDto> getAllEnterprises() {
        return enterpriseRepository.findAll().stream().map(Enterprise::toResponseDto).toList();
    }
}
