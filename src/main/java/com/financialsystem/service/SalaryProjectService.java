package com.financialsystem.service;

import com.financialsystem.domain.model.Currency;
import com.financialsystem.domain.model.Enterprise;
import com.financialsystem.domain.model.SalaryProject;
import com.financialsystem.domain.model.account.Account;
import com.financialsystem.domain.model.account.SalaryAccount;
import com.financialsystem.domain.model.transaction.Transaction;
import com.financialsystem.domain.model.transaction.TransactionType;
import com.financialsystem.domain.model.user.*;
import com.financialsystem.domain.status.SalaryProjectStatus;
import com.financialsystem.dto.database.user.SpecialistDatabaseDto;
import com.financialsystem.dto.request.EmployeeRequestForCreatingSalaryProject;
import com.financialsystem.dto.request.EmployeeRequestForSalaryProject;
import com.financialsystem.dto.request.SalaryProjectRequest;
import com.financialsystem.dto.response.EmployeeResponseForSalaryProject;
import com.financialsystem.dto.response.EnterpriseResponseDto;
import com.financialsystem.dto.response.SalaryProjectDetailsResponseDto;
import com.financialsystem.dto.response.SalaryProjectResponseDto;
import com.financialsystem.exception.custom.NotFoundException;
import com.financialsystem.repository.EnterpriseRepository;
import com.financialsystem.repository.SalaryProjectRepository;
import com.financialsystem.repository.TransactionRepository;
import com.financialsystem.repository.account.AccountRepository;
import com.financialsystem.repository.account.SalaryAccountRepository;
import com.financialsystem.repository.user.ClientRepository;
import com.financialsystem.repository.user.SpecialistRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SalaryProjectService {

    private final SpecialistRepository specialistRepository;
    private final EntityFinder entityFinder;
    private final EnterpriseRepository enterpriseRepository;
    private final SalaryProjectRepository salaryProjectRepository;
    private final ClientRepository clientRepository;
    private final SalaryAccountRepository salaryAccountRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public SalaryProjectService(SpecialistRepository specialistRepository, EntityFinder entityFinder,
                                EnterpriseRepository enterpriseRepository, SalaryProjectRepository salaryProjectRepository,
                                ClientRepository clientRepository, SalaryAccountRepository salaryAccountRepository,
                                AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.specialistRepository = specialistRepository;
        this.entityFinder = entityFinder;
        this.enterpriseRepository = enterpriseRepository;
        this.salaryProjectRepository = salaryProjectRepository;
        this.clientRepository = clientRepository;
        this.salaryAccountRepository = salaryAccountRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Long createRequest(BankingUserDetails userDetails, SalaryProjectRequest request) {
        Currency currency = Currency.valueOf(request.currency());
        Specialist specialist = entityFinder.findEntityById(userDetails.getId(), specialistRepository, "Специалист стороннего предприятия");
        Long enterpriseId = specialist.getEnterpriseId();
        Enterprise enterprise = entityFinder.findEntityById(enterpriseId, enterpriseRepository, "Предприятие");

        SalaryProject salaryProject = SalaryProject.createSalaryProjectRequest(enterpriseId, enterprise.getBankId(), currency);
        Long salaryProjectId = salaryProjectRepository.create(salaryProject);

        List<EmployeeRequestForCreatingSalaryProject> employees = request.employees();

        employees.forEach(employee -> addEmployeeToSalaryProject(
                new EmployeeRequestForSalaryProject(salaryProjectId, employee.passportSeriesNumber(), employee.salaryAmount()), userDetails));

        return salaryProjectId;
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

        salaryProject.checkStatusToAddEmployee();

        Client client = clientRepository.findByPassportSeriesNumber(request.passportSeriesNumber()).
                orElseThrow(() -> new NotFoundException("Клиент с серийным номером пасспорта " +
                request.passportSeriesNumber() + " не найден"));

        client.checkRole(Role.CLIENT);

        SalaryAccount salaryAccount = SalaryAccount.create(client.getId(), enterprise.getBankId(),
                salaryProject.getCurrency(), salaryProject.getId(), request.salaryAmount());

        Long salaryAccountId = salaryAccountRepository.create(salaryAccount);

        return new EmployeeResponseForSalaryProject(
                salaryProject.getId(), enterpriseId, enterprise.getBankId(), salaryAccountId, client.toDto().getFullName(), request.passportSeriesNumber()
        );
    }

    @Transactional
    public Long approveProject(Long pendingProjectId) {
        SalaryProject salaryProject = entityFinder.findEntityById(pendingProjectId, salaryProjectRepository, "Заявка на зарплатный проект");
        List<SalaryAccount> accounts = salaryAccountRepository.findAllBySalaryProjectId(pendingProjectId);
        accounts.forEach(SalaryAccount::approveSalaryAccountStatus);
        salaryProject.approveProjectRequest();
        accounts.forEach(salaryAccountRepository::update);
        return salaryProjectRepository.update(salaryProject);
    }

    //scheduled
    @Transactional
    public void executeProjectMonthlySalary() {
        List<SalaryProject> activeProjects = salaryProjectRepository.findAll()
                .stream()
                .filter(sp -> sp.isStatus(SalaryProjectStatus.ACTIVE))
                .toList();

        for (SalaryProject project : activeProjects) {
            Enterprise enterprise = entityFinder.findEntityById(project.getEnterpriseId(), enterpriseRepository, "Предприятие");
            Account enterprisePayrollAccount = entityFinder.findEntityById(enterprise.getPayrollAccountId(),
                    accountRepository, "Счет предприятия для выплаты зарплат");

            List<SalaryAccount> salaryAccounts = salaryAccountRepository.findAllBySalaryProjectId(project.getId());

            for (SalaryAccount account : salaryAccounts) {
                BigDecimal salaryAmount = account.getSalaryAmount();
                Long mainAccountId = salaryAccountRepository.findAccountIdById(account.getId());
                Transaction salaryTransaction = Transaction.create(
                        enterprise.getPayrollAccountId(), TransactionType.ACCOUNT, mainAccountId, TransactionType.ACCOUNT, salaryAmount);

                enterprisePayrollAccount.withdraw(salaryAmount);
                account.replenish(salaryAmount);

                transactionRepository.create(salaryTransaction);
                accountRepository.update(enterprisePayrollAccount);
                salaryAccountRepository.update(account);
            }
        }
    }

    public List<SalaryProjectResponseDto> getAllSalaryProjects() {
        List<SalaryProject> salaryProjects = salaryProjectRepository.findAll();
        return salaryProjects.stream()
                .map(SalaryProject::toResponseDto)
                .toList();
    }

    public SalaryProjectDetailsResponseDto getSalaryProjectDetails(Long salaryPriojectId) {
        SalaryProject salaryProject = entityFinder.findEntityById(salaryPriojectId, salaryProjectRepository, "Зарплатный проект");
        List<SalaryAccount> salaryAccounts = salaryAccountRepository.findAllBySalaryProjectId(salaryProject.getId());
        Enterprise enterprise = entityFinder.findEntityById(salaryProject.getEnterpriseId(), enterpriseRepository, "Предприятие");
        Account enterprisePayrollAccount = entityFinder.findEntityById(enterprise.getPayrollAccountId(),
                accountRepository, "Счет предприятия для выплаты зарплат");
        return new SalaryProjectDetailsResponseDto(
                salaryAccounts.stream()
                        .map(SalaryAccount::toSalaryAccountResponseDto)
                        .toList(),
                enterprisePayrollAccount.toAccountResponseDto(),
                salaryProject.toResponseDto()
        );
    }

    public boolean validateOwner(Long salaryProjectId, BankingUserDetails userDetails) {
        SpecialistDatabaseDto specialist = specialistRepository.findByName(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(userDetails.getUsername()));
        SalaryProject salaryProject = entityFinder.findEntityById(salaryProjectId, salaryProjectRepository, "Зарплатный проект");
        return salaryProject.getEnterpriseId().equals(specialist.getEnterpriseId());
    }

    public List<SalaryProjectResponseDto> getProjectsBySpecialist(BankingUserDetails userDetails) {
        Specialist specialist = entityFinder.findEntityById(userDetails.getId(), specialistRepository, "Специалист стороннего предприятия");
        Enterprise enterprise = entityFinder.findEntityById(specialist.getEnterpriseId(), enterpriseRepository, "Предприятие");
        return salaryProjectRepository.findAllByEnterpriseId(enterprise.getId()).
               stream().map(SalaryProject::toResponseDto)
                .toList();
    }
}
