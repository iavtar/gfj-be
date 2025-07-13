package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.Client;
import com.iavtar.gfj_be.model.request.CreateClientRequest;
import com.iavtar.gfj_be.model.response.PagedUserResponse;
import com.iavtar.gfj_be.repository.AppUserRepository;
import com.iavtar.gfj_be.repository.ClientRepository;
import com.iavtar.gfj_be.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AgentService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private CommonUtil commonUtil;

    public boolean existsByClientName(String clientName) {
        return clientRepository.existsByClientName(clientName);
    }

    public boolean existsByEmail(String email) {
        return clientRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return clientRepository.existsByPhoneNumber(phoneNumber);
    }

    public Client createClient(CreateClientRequest request) {
        log.info("Agent creating client: {}", request.getClientName());

        Client savedClient = clientRepository.save(Client.builder()
                .clientName(request.getClientName())
                .businessLogoUrl(request.getBusinessLogoUrl())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .businessAddress(request.getBusinessAddress())
                .shippingAddress(request.getShippingAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .einNumber(request.getEinNumber())
                .taxId(request.getTaxId())
                .diamondSettingPrice(request.getDiamondSettingPrice())
                .goldWastagePercentage(request.getGoldWastagePercentage())
                .profitAndLabourPercentage(request.getProfitAndLabourPercentage())
                .cadCamWaxPrice(request.getCadCamWaxPrice())
                .agentId(request.getAgentId())
                .build());

        log.info("Client created successfully with ID: {} by agent: {}",
                savedClient.getId(), request.getAgentId());

        return savedClient;
    }

    public Client getClientByName(String clientName) {
        log.info("Getting client by name: {}", clientName);
        return commonUtil.findClientByName(clientName);
    }

    public PagedUserResponse<Client> getAllClients(int offset, int size, String sortBy) {
        log.info("Getting all clients with pagination");
        return commonUtil.findAllClients(offset, size, sortBy);
    }

    public PagedUserResponse<Client> getClientsByAgent(Long agentId, int offset, int size, String sortBy) {
        log.info("Getting clients for agent: {}", agentId);
        return commonUtil.findClientsByAgent(agentId, offset, size, sortBy);
    }


    private Client convertToEntity(CreateClientRequest request) {
        Client client = new Client();
        client.setClientName(request.getClientName());
        client.setBusinessLogoUrl(request.getBusinessLogoUrl());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setBusinessAddress(request.getBusinessAddress());
        client.setShippingAddress(request.getShippingAddress());
        client.setCity(request.getCity());
        client.setState(request.getState());
        client.setCountry(request.getCountry());
        client.setZipCode(request.getZipCode());
        client.setEinNumber(request.getEinNumber());
        client.setTaxId(request.getTaxId());
        client.setDiamondSettingPrice(request.getDiamondSettingPrice());
        client.setGoldWastagePercentage(request.getGoldWastagePercentage());
        client.setProfitAndLabourPercentage(request.getProfitAndLabourPercentage());
        client.setCadCamWaxPrice(request.getCadCamWaxPrice());
        client.setAgentId(request.getAgentId());
        return client;
    }
}