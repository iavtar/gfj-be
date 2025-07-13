package com.iavtar.gfj_be.controller;

import com.iavtar.gfj_be.entity.Client;
import com.iavtar.gfj_be.model.request.CreateClientRequest;
import com.iavtar.gfj_be.model.response.PagedUserResponse;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @PostMapping("/client")
    public ResponseEntity<?> createClient(@RequestBody CreateClientRequest request) {
        try {
            log.info("Creating new client with clientName: {}", request.getClientName());

            if (agentService.existsByClientName(request.getClientName())) {
                log.error("Validation failed - client name already exists: {}", request.getClientName());
                ServiceResponse errorResponse = ServiceResponse.builder().message("Client name already exists").build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (agentService.existsByEmail(request.getEmail())) {
                log.error("Validation failed - email already exists: {}", request.getEmail());
                ServiceResponse errorResponse = ServiceResponse.builder().message("Email already exists").build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (agentService.existsByPhoneNumber(request.getPhoneNumber())) {
                log.error("Validation failed - phone number already exists: {}", request.getPhoneNumber());
                ServiceResponse errorResponse = ServiceResponse.builder().message("Phone number already exists").build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Client createdClient = agentService.createClient(request);
            log.info("Client created successfully with ID: {}", createdClient.getId());

            ServiceResponse response = ServiceResponse.builder().message("Client created successfully").build();
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Validation error creating client: {}", e.getMessage());
            ServiceResponse errorResponse = ServiceResponse.builder().message(e.getMessage()).build();
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            log.error("Error creating client: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error creating client: " + e.getMessage()).build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/client")
    public ResponseEntity<?> getClientByName(@RequestParam("clientName") String clientName) {
        try {
            log.info("Getting client by name: {}", clientName);

            Client client = agentService.getClientByName(clientName);
            if (client == null) {
                ServiceResponse errorResponse = ServiceResponse.builder().message("Client not found: " + clientName).build();
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(client);

        } catch (Exception e) {
            log.error("Error getting client by name: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error getting client: " + clientName).build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/clients")
    public ResponseEntity<?> getAllClients(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        try {
            log.info("Getting all clients with pagination: offset={}, size={}, sortBy={}", offset, size, sortBy);

            PagedUserResponse<Client> response = agentService.getAllClients(offset, size, sortBy);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting all clients: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error getting all clients").build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/clients/my")
    public ResponseEntity<?> getMyClients(@RequestParam("agentId") Long agentId, @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sortBy) {
        try {
            log.info("Getting clients for agent: {} with pagination: offset={}, size={}, sortBy={}", agentId, offset, size, sortBy);

            PagedUserResponse<Client> response = agentService.getClientsByAgent(agentId, offset, size, sortBy);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting clients for agent {}: {}", agentId, e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error getting clients for agent").build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}

