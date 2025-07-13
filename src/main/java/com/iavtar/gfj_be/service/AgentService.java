package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.Client;
import com.iavtar.gfj_be.model.request.CreateClientRequest;
import com.iavtar.gfj_be.repository.AppUserRepository;
import com.iavtar.gfj_be.repository.ClientRepository;
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

        // Convert DTO to Entity
        Client client = convertToEntity(request);

        // Save the client
        Client savedClient = clientRepository.save(client);

        log.info("Client created successfully with ID: {} by agent: {}", savedClient.getId(), request.getClientName());

        return savedClient;
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
