package com.iavtar.gfj_be.controller;

import com.iavtar.gfj_be.entity.Client;
import com.iavtar.gfj_be.model.request.CreateClientRequest;
import com.iavtar.gfj_be.model.request.CreateUserRequest;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.service.AgentService;
import com.iavtar.gfj_be.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @Autowired
    private CommonUtil commonUtil;

    @PostMapping("/user")
    public ResponseEntity<?> createClient(@RequestBody CreateClientRequest request) {
        try {

            log.info("Creating new client with clientName: {}", request.getClientName());

            // Check if client name already exists
            if (agentService.existsByClientName(request.getClientName())) {
                log.error("Validation failed - client name already exists: {}", request.getClientName());
                ServiceResponse errorResponse = ServiceResponse.builder().message("Client name already exists").build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Check if email already exists
            if (agentService.existsByEmail(request.getEmail())) {
                log.error("Validation failed - email already exists: {}", request.getEmail());
                ServiceResponse errorResponse = ServiceResponse.builder().message("Email already exists").build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Check if phone number already exists
            if (agentService.existsByPhoneNumber(request.getPhoneNumber())) {
                log.error("Validation failed - phone number already exists: {}", request.getPhoneNumber());
                ServiceResponse errorResponse = ServiceResponse.builder().message("Phone number already exists").build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Create the client
            Client createdClient = agentService.createClient(request);

            log.info("Client created successfully with ID: {}", createdClient.getId());

            ServiceResponse response = ServiceResponse.builder()
                    .message("Client created successfully")
                    .data(createdClient)
                    .build();

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
}
