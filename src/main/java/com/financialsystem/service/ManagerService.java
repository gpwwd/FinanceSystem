package com.financialsystem.service;

import com.financialsystem.domain.model.user.Client;
import com.financialsystem.domain.model.user.PendingClient;
import com.financialsystem.dto.database.user.PendingClientDatabaseDto;
import com.financialsystem.dto.response.PendingClientResponseDto;
import com.financialsystem.mapper.ClientMapper;
import com.financialsystem.repository.user.ClientRepository;
import com.financialsystem.repository.user.PendingClientRepository;
import com.financialsystem.util.EntityFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@PreAuthorize("hasAuthority('MANAGER')")
public class ManagerService {

    private final PendingClientRepository pendingClientRepository;
    private final ClientRepository clientRepository;
    private final EntityFinder entityFinder;

    @Autowired
    public ManagerService(PendingClientRepository pendingClientRepository, ClientRepository clientRepository,
                          EntityFinder entityFinder) {
        this.pendingClientRepository = pendingClientRepository;
        this.clientRepository = clientRepository;
        this.entityFinder = entityFinder;
    }

    @Transactional
    public Long approveClient(Long pendingClientId
    //Long managerId // достать из userDetails
    ) {
        PendingClient pendingClient = entityFinder.findEntityById(pendingClientId, pendingClientRepository, "Клиент ожидающий регистрацию");
        //manager.approveClientRegistration(pendingClient); // или перенести внутрь класса
        pendingClient.setApprovedStatus();
        PendingClientDatabaseDto dto = pendingClient.toDto();
        Client client = Client.create(dto);

        pendingClientRepository.delete(pendingClient);
        return clientRepository.create(client);
    }

    @Transactional
    public Long rejectClient(Long pendingClientId) {
        PendingClient pendingClient = entityFinder.findEntityById(pendingClientId, pendingClientRepository, "Клиент ожидающий регистрацию");
        pendingClient.rejectRegistration();
        return pendingClientRepository.update(pendingClient);
    }

    @Transactional(readOnly = true)
    public List<PendingClientResponseDto> getAllPendingClients() {
        return pendingClientRepository.findAll().stream()
                .map(ClientMapper::toPendingClientResponseDto)
                .toList();
    }
}
