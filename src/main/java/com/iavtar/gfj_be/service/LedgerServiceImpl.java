package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.Client;
import com.iavtar.gfj_be.entity.LedgerTransaction;
import com.iavtar.gfj_be.exception.LedgerException;
import com.iavtar.gfj_be.model.request.CreateLedgerTransactionRequest;
import com.iavtar.gfj_be.model.request.UpdateLedgerTransactionRequest;
import com.iavtar.gfj_be.model.response.LedgerTransactionWithClientResponse;
import com.iavtar.gfj_be.model.response.PagedLedgerTransactionWithClientResponse;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.repository.ClientRepository;
import com.iavtar.gfj_be.repository.LedgerRespository;
import com.iavtar.gfj_be.utility.CustomIdGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LedgerServiceImpl implements LedgerService {

    @Autowired
    private LedgerRespository ledgerRespository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public ResponseEntity<?> createLedgerTransaction(CreateLedgerTransactionRequest request) {
        try {
            ledgerRespository.save(
                    LedgerTransaction.builder()
                            .transactionId(CustomIdGeneratorUtil.generateId())
                            .clientId(request.getClientId())
                            .amount(request.getAmount())
                            .paymentMethod(request.getPaymentMethod())
                            .orderId(request.getOrderId())
                            .category(request.getCategory())
                            .reference(request.getReference())
                            .paymentStatus(request.getPaymentStatus())
                            .note(request.getNote())
                            .description(request.getDescription())
                            .build()
            );
            return new ResponseEntity<>(ServiceResponse.builder().build(), HttpStatus.OK);
        } catch (Exception e) {
            throw new LedgerException(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<PagedLedgerTransactionWithClientResponse> getAllLedgerTransactions(int offset, int size) {
        try {
            if (offset < 0) {
                offset = 0;
            }
            if (size <= 0 || size > 100) {
                size = 20;
            }
            Pageable pageable = PageRequest.of(offset / size, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<LedgerTransaction> page = ledgerRespository.findAll(pageable);            
            List<Long> clientIds = page.getContent().stream()
                    .map(LedgerTransaction::getClientId)
                    .distinct()
                    .collect(Collectors.toList());            
            Map<Long, Client> clientMap = clientRepository.findAllById(clientIds).stream()
                    .collect(Collectors.toMap(Client::getId, client -> client));            
            List<LedgerTransactionWithClientResponse> transactionsWithClient = page.getContent().stream()
                    .map(transaction -> {
                        Client client = clientMap.get(transaction.getClientId());
                        return LedgerTransactionWithClientResponse.from(transaction, client);
                    })
                    .collect(Collectors.toList());            
            Page<LedgerTransactionWithClientResponse> customPage = new org.springframework.data.domain.PageImpl<>(
                    transactionsWithClient, pageable, page.getTotalElements());
            PagedLedgerTransactionWithClientResponse response = PagedLedgerTransactionWithClientResponse.from(customPage, offset, size);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching ledger transactions: {}", e.getMessage());
            throw new LedgerException("Failed to fetch ledger transactions: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> updateLedgerTransaction(String transactionId, UpdateLedgerTransactionRequest request) {
        try {
            LedgerTransaction existingTransaction = ledgerRespository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new LedgerException("Transaction not found with transactionId: " + transactionId));
            if (request.getClientId() != null) {
                existingTransaction.setClientId(request.getClientId());
            }
            if (request.getAmount() != null) {
                existingTransaction.setAmount(request.getAmount());
            }
            if (request.getPaymentMethod() != null) {
                existingTransaction.setPaymentMethod(request.getPaymentMethod());
            }
            if (request.getOrderId() != null) {
                existingTransaction.setOrderId(request.getOrderId());
            }
            if (request.getCategory() != null) {
                existingTransaction.setCategory(request.getCategory());
            }
            if (request.getReference() != null) {
                existingTransaction.setReference(request.getReference());
            }
            if (request.getPaymentStatus() != null) {
                existingTransaction.setPaymentStatus(request.getPaymentStatus());
            }
            if (request.getNote() != null) {
                existingTransaction.setNote(request.getNote());
            }
            if (request.getDescription() != null) {
                existingTransaction.setDescription(request.getDescription());
            }
            LedgerTransaction updatedTransaction = ledgerRespository.save(existingTransaction);
            log.info("Successfully updated ledger transaction with transactionId: {}", transactionId);
            return new ResponseEntity<>(ServiceResponse.builder()
                    .message("Transaction updated successfully")
                    .data(updatedTransaction)
                    .build(), HttpStatus.OK);
        } catch (LedgerException e) {
            log.error("Error updating ledger transaction: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating ledger transaction: {}", e.getMessage());
            throw new LedgerException("Failed to update ledger transaction: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteLedgerTransaction(String transactionId) {
        try {
            LedgerTransaction existingTransaction = ledgerRespository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new LedgerException("Transaction not found with transactionId: " + transactionId));
            ledgerRespository.delete(existingTransaction);
            log.info("Successfully deleted ledger transaction with transactionId: {}", transactionId);
            return new ResponseEntity<>(ServiceResponse.builder()
                    .message("Transaction deleted successfully")
                    .build(), HttpStatus.OK);
        } catch (LedgerException e) {
            log.error("Error deleting ledger transaction: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting ledger transaction: {}", e.getMessage());
            throw new LedgerException("Failed to delete ledger transaction: " + e.getMessage());
        }
    }

}
