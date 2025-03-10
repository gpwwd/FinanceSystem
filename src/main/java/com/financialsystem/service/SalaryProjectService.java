package com.financialsystem.service;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.model.Enterprise;
import com.financialsystem.domain.model.SalaryProject;
import com.financialsystem.domain.model.account.SalaryAccount;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.domain.model.user.Client;
import com.financialsystem.domain.model.user.Role;
import com.financialsystem.domain.model.user.Specialist;
import com.financialsystem.dto.request.EmployeeRequestForSalaryProject;
import com.financialsystem.dto.response.EmployeeResponseForSalaryProject;
import com.financialsystem.exception.custom.NotFoundException;
import com.financialsystem.repository.EnterpriseRepository;
import com.financialsystem.repository.SalaryProjectRepository;
import com.financialsystem.repository.account.SalaryAccountRepository;
import com.financialsystem.repository.user.ClientRepository;
import com.financialsystem.repository.user.SpecialistRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.util.List;

@Service
public class SalaryProjectService {

    private final SpecialistRepository specialistRepository;
    private final EntityFinder entityFinder;
    private final EnterpriseRepository enterpriseRepository;
    private final SalaryProjectRepository salaryProjectRepository;
    private final ClientRepository clientRepository;
    private final SalaryAccountRepository salaryAccountRepository;

    @Autowired
    public SalaryProjectService(SpecialistRepository specialistRepository, EntityFinder entityFinder,
                                EnterpriseRepository enterpriseRepository, SalaryProjectRepository salaryProjectRepository,
                                ClientRepository clientRepository, SalaryAccountRepository salaryAccountRepository) {
        this.specialistRepository = specialistRepository;
        this.entityFinder = entityFinder;
        this.enterpriseRepository = enterpriseRepository;
        this.salaryProjectRepository = salaryProjectRepository;
        this.clientRepository = clientRepository;
        this.salaryAccountRepository = salaryAccountRepository;
    }

    @Transactional
    public Long createRequest(BankingUserDetails userDetails, Currency currency) {
        Specialist specialist = entityFinder.findEntityById(userDetails.getId(), specialistRepository, "Специалист стороннего предприятия");
        Long enterpriseId = specialist.getEnterpriseId();
        Enterprise enterprise = entityFinder.findEntityById(enterpriseId, enterpriseRepository, "Предприятие");
        SalaryProject salaryProject = SalaryProject.createSalaryProjectRequest(enterpriseId, enterprise.getBankId(), currency);
        return salaryProjectRepository.create(salaryProject);
    }

    @Transactional
    public EmployeeResponseForSalaryProject addEmployeeToSalaryProject(EmployeeRequestForSalaryProject request, BankingUserDetails userDetails) {
        Long enterpriseId = entityFinder.
                findEntityById(userDetails.getId(), specialistRepository, "Специалист стороннего предприятия").
                getEnterpriseId();

        Enterprise enterprise = entityFinder.findEntityById(enterpriseId, enterpriseRepository, "Предприятие");

        List<SalaryProject> salaryProjects = salaryProjectRepository.findAllByEnterpriseId(enterpriseId);

        SalaryProject salaryProject = salaryProjects.stream()
                .filter(sp -> sp.getId().equals(request.salaryProjectId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Salary project with ID " + request.salaryProjectId()
                        + " not found among projects for enterpriseId " + enterpriseId));

        Client client = clientRepository.findByPassportSeriesNumber(request.passportSeriesNumber()).
                orElseThrow(() -> new NotFoundException("Клиент с серийным номером пасспорта " +
                request.passportSeriesNumber() + " не найден"));
        client.checkRole(Role.CLIENT);

        SalaryAccount salaryAccount = SalaryAccount.create(client.getId(), enterprise.getBankId(),
                salaryProject.getCurrency(), salaryProject.getId());
        Long salaryAccountId = salaryAccountRepository.create(salaryAccount);

        return new EmployeeResponseForSalaryProject(
                salaryProject.getId(), enterpriseId, enterprise.getBankId(), salaryAccountId, client.toDto().getFullName(), request.passportSeriesNumber()
        );
    }
}
