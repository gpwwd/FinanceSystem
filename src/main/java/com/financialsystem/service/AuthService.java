package com.financialsystem.service;

import com.financialsystem.domain.model.Enterprise;
import com.financialsystem.domain.model.account.EnterpriseAccount;
import com.financialsystem.domain.model.user.BankingUserDetails;
import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.domain.model.user.Specialist;
import com.financialsystem.dto.database.EnterpriseDatabaseDto;
import com.financialsystem.dto.database.user.SpecialistDatabaseDto;
import com.financialsystem.dto.request.ClientRegistrationRequest;
import com.financialsystem.dto.request.EnterpriseRegistrationRequest;
import com.financialsystem.dto.request.SpecialistRegistrationRequest;
import com.financialsystem.dto.response.UserAuthResponseDto;
import com.financialsystem.exception.custom.BadRequestException;
import com.financialsystem.repository.BankRepository;
import com.financialsystem.repository.account.AccountRepository;
import com.financialsystem.repository.user.PendingClientRepository;
import com.financialsystem.repository.user.SpecialistRepository;
import com.financialsystem.security.repository.UserDetailsRepository;
import com.financialsystem.security.service.JwtService;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.financialsystem.repository.EnterpriseRepository;

import java.util.Optional;

@Service
public class AuthService {

    private final PendingClientRepository pendingClientRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SpecialistRepository specialistRepository;
    private final EntityFinder entityFinder;
    private final EnterpriseRepository enterpriseRepository;
    private final BankRepository bankRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public AuthService(PendingClientRepository pendingClientRepository, UserDetailsRepository userDetailsRepository,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       SpecialistRepository specialistRepository, EntityFinder entityFinder,
                       EnterpriseRepository enterpriseRepository, BankRepository bankRepository, AccountRepository accountRepository) {
        this.pendingClientRepository = pendingClientRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.specialistRepository = specialistRepository;
        this.entityFinder = entityFinder;
        this.enterpriseRepository = enterpriseRepository;
        this.bankRepository = bankRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Long registerClient(ClientRegistrationRequest request) {
        checkIfUserInTheSystem(request.fullName());
        PendingClient pendingClient = PendingClient.create(request);
        return pendingClientRepository.create(pendingClient);
    }

    private void checkIfUserInTheSystem(String username) {
        Optional<BankingUserDetails> userOptional = userDetailsRepository.findByName(username);

        if (userOptional.isPresent()) {
            throw new BadRequestException("User " + username + " is already in use");
        }

        Optional<PendingClient> pendingClientOptional = pendingClientRepository.findByName(username);

        if (pendingClientOptional.isPresent()) {
            throw new BadRequestException("User with name '" + username + "' is already pending for verification.");
        }
    }

    public UserAuthResponseDto login(String username, String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        BankingUserDetails userDetails = (BankingUserDetails) authentication.getPrincipal();

        var token = jwtService.generateToken(userDetails);

        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::toString)
                .toList();

        return new UserAuthResponseDto(
                userDetails.getId(), userDetails.getUsername(), token, authorities.getFirst()
        );
    }

    public Long registerSpecialist(SpecialistRegistrationRequest request) {
        Optional<SpecialistDatabaseDto> existingSpecialist = specialistRepository.findByName(request.fullName());
        if (existingSpecialist.isPresent()) {
            throw new BadRequestException("Specialist " + request.fullName() + " is already in use");
        }

        entityFinder.findEntityById(request.enterpriseId(), enterpriseRepository, "Предприятие");

        Specialist specialist = Specialist.create(request);
        return specialistRepository.create(specialist);
    }

    @Transactional
    public Long registerEnterprise(EnterpriseRegistrationRequest request) {
        Optional<EnterpriseDatabaseDto> existingNameEnterprise = enterpriseRepository.findByLegalName(request.legalName());
        if (existingNameEnterprise.isPresent()) {
            throw new BadRequestException("Enterprise with legal name" + request.legalName() + " is already in use");
        }

        Optional<EnterpriseDatabaseDto> existingUnpEnterprise = enterpriseRepository.findByUnp(request.unp());
        if (existingUnpEnterprise.isPresent()) {
            throw new BadRequestException("Enterprise with unp " + request.unp() + " is already in use");
        }

        entityFinder.findEntityById(request.bankId(), bankRepository, "Банк");

        Enterprise enterprise = Enterprise.create(request, null);
        Long createdEnterpriseId = enterpriseRepository.create(enterprise);

        EnterpriseAccount enterprisePayrollAccount = EnterpriseAccount.create(createdEnterpriseId,
                request.bankId(), request.enterpriseAccountRegistrationRequest().currency());
        Long enterprisePayrollAccountId = accountRepository.create(enterprisePayrollAccount);

        Enterprise createdEnterprise = entityFinder.findEntityById(createdEnterpriseId, enterpriseRepository, "Созданное предприятие");

        createdEnterprise.connectPayrollAccount(enterprisePayrollAccountId);
        return enterpriseRepository.update(createdEnterprise);
    }
}
