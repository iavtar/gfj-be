package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.Client;
import com.iavtar.gfj_be.entity.ClientLedger;
import com.iavtar.gfj_be.exception.LedgerException;
import com.iavtar.gfj_be.model.request.CreateClientLedgerRequest;
import com.iavtar.gfj_be.model.response.ClientLedgerResponse;
import com.iavtar.gfj_be.model.response.ClientLedgerSummaryResponse;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.repository.ClientLedgerRepository;
import com.iavtar.gfj_be.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClientLedgerServiceImpl implements ClientLedgerService {

    @Autowired
    private ClientLedgerRepository clientLedgerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public ResponseEntity<?> createTransaction(CreateClientLedgerRequest request) {
        try {
            // Validate client exists
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new LedgerException("Client not found with ID: " + request.getClientId()));

            // Generate unique transaction ID
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Create ledger transaction
            ClientLedger ledger = ClientLedger.builder()
                    .clientId(request.getClientId())
                    .transactionId(transactionId)
                    .amount(request.getAmount())
                    .transactionType(request.getTransactionType())
                    .description(request.getDescription())
                    .reference(request.getReference())
                    .note(request.getNote())
                    .build();

            ClientLedger savedLedger = clientLedgerRepository.save(ledger);
            log.info("Created ledger transaction: {} for client: {}", transactionId, client.getClientName());

            return new ResponseEntity<>(ServiceResponse.builder()
                    .message("Transaction created successfully")
                    .data(ClientLedgerResponse.from(savedLedger))
                    .build(), HttpStatus.CREATED);

        } catch (LedgerException e) {
            log.error("Error creating transaction: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating transaction: {}", e.getMessage());
            throw new LedgerException("Failed to create transaction: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ClientLedgerSummaryResponse> getClientLedger(Long clientId, int page, int size) {
        try {
            // Validate client exists
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new LedgerException("Client not found with ID: " + clientId));

            // Validate pagination parameters
            if (page < 0) page = 0;
            if (size <= 0 || size > 100) size = 20;

            Pageable pageable = PageRequest.of(page, size);
            Page<ClientLedger> ledgerPage = clientLedgerRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable);

            // Get client balance and transaction count
            BigDecimal balance = clientLedgerRepository.getClientBalance(clientId);
            Long transactionCount = clientLedgerRepository.getTransactionCount(clientId);

            // Convert to response DTOs
            List<ClientLedgerResponse> transactions = ledgerPage.getContent().stream()
                    .map(ClientLedgerResponse::from)
                    .collect(Collectors.toList());

            ClientLedgerSummaryResponse response = ClientLedgerSummaryResponse.builder()
                    .clientId(clientId)
                    .clientName(client.getClientName())
                    .currentBalance(balance)
                    .totalTransactions(transactionCount)
                    .recentTransactions(transactions)
                    .page(page)
                    .size(size)
                    .totalElements(ledgerPage.getTotalElements())
                    .totalPages(ledgerPage.getTotalPages())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (LedgerException e) {
            log.error("Error fetching client ledger: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching client ledger: {}", e.getMessage());
            throw new LedgerException("Failed to fetch client ledger: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getTransaction(String transactionId) {
        try {
            ClientLedger ledger = clientLedgerRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new LedgerException("Transaction not found with ID: " + transactionId));

            return new ResponseEntity<>(ServiceResponse.builder()
                    .message("Transaction retrieved successfully")
                    .data(ClientLedgerResponse.from(ledger))
                    .build(), HttpStatus.OK);

        } catch (LedgerException e) {
            log.error("Error fetching transaction: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching transaction: {}", e.getMessage());
            throw new LedgerException("Failed to fetch transaction: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteTransaction(String transactionId) {
        try {
            ClientLedger ledger = clientLedgerRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new LedgerException("Transaction not found with ID: " + transactionId));

            clientLedgerRepository.delete(ledger);
            log.info("Deleted ledger transaction: {}", transactionId);

            return new ResponseEntity<>(ServiceResponse.builder()
                    .message("Transaction deleted successfully")
                    .build(), HttpStatus.OK);

        } catch (LedgerException e) {
            log.error("Error deleting transaction: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting transaction: {}", e.getMessage());
            throw new LedgerException("Failed to delete transaction: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> getClientBalance(Long clientId) {
        try {
            // Validate client exists
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new LedgerException("Client not found with ID: " + clientId));

            BigDecimal balance = clientLedgerRepository.getClientBalance(clientId);

            return new ResponseEntity<>(ServiceResponse.builder()
                    .message("Client balance retrieved successfully")
                    .data(balance)
                    .build(), HttpStatus.OK);

        } catch (LedgerException e) {
            log.error("Error fetching client balance: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching client balance: {}", e.getMessage());
            throw new LedgerException("Failed to fetch client balance: " + e.getMessage());
        }
    }
}
