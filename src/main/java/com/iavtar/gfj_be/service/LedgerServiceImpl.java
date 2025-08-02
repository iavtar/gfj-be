package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.Client;
import com.iavtar.gfj_be.entity.LedgerTransaction;
import com.iavtar.gfj_be.entity.Quotation;
import com.iavtar.gfj_be.exception.LedgerException;
import com.iavtar.gfj_be.model.request.CreateLedgerTransactionRequest;
import com.iavtar.gfj_be.model.request.UpdateLedgerTransactionRequest;
import com.iavtar.gfj_be.model.response.LedgerTransactionWithClientResponse;
import com.iavtar.gfj_be.model.response.PagedLedgerTransactionWithClientResponse;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.repository.ClientRepository;
import com.iavtar.gfj_be.repository.LedgerRespository;
import com.iavtar.gfj_be.repository.QuotationRepository;
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

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private LedgerIdGeneratorService idGeneratorService;

    @Override
    public ResponseEntity<?> createLedgerTransaction(CreateLedgerTransactionRequest request) {
        try {
            ledgerRespository.save(
                    LedgerTransaction.builder()
                            .transactionId(idGeneratorService.generateId())
                            .amount(request.getAmount())
                            .paymentMethod(request.getPaymentMethod())
                            .orderId(request.getOrderId())
                            .category(request.getCategory())
                            .reference(request.getReference())
                            .paymentStatus(request.getPaymentStatus())
                            .note(request.getNote())
                            .build()
            );
            return new ResponseEntity<>(ServiceResponse.builder()
                    .message("Ledger Transaction added successfully!")
                    .build(), HttpStatus.OK);
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
            
            List<LedgerTransactionWithClientResponse> transactionsWithClient = page.getContent().stream()
                    .map(transaction -> {
                        Client client = null;
                        
                        // Get quotation and client information if reference exists
                        if (transaction.getReference() != null && !transaction.getReference().trim().isEmpty()) {
                            log.debug("Looking up quotation with quotationId: {}", transaction.getReference());
                            Optional<Quotation> quotation = quotationRepository.findByQuotationId(transaction.getReference());
                            if (quotation.isPresent()) {
                                // Get client information from quotation's clientId
                                if (quotation.get().getClientId() != null) {
                                    Optional<Client> clientOptional = clientRepository.findById(quotation.get().getClientId());
                                    if (clientOptional.isPresent()) {
                                        client = clientOptional.get();
                                        log.debug("Found client from quotation: {}", client.getClientName());
                                    } else {
                                        log.debug("Client not found for clientId: {}", quotation.get().getClientId());
                                    }
                                } else {
                                    log.debug("Quotation has no clientId for reference: {}", transaction.getReference());
                                }
                                
                                // Set description from quotation
                                if (quotation.get().getDescription() != null && !quotation.get().getDescription().trim().isEmpty()) {
                                    log.debug("Found quotation description: {}", quotation.get().getDescription());
                                    transaction.setDescription(quotation.get().getDescription());
                                } else {
                                    log.debug("No description found in quotation for reference: {}", transaction.getReference());
                                    transaction.setDescription("Transaction for quotation: " + transaction.getReference());
                                }
                            } else {
                                log.debug("No quotation found for reference: {}", transaction.getReference());
                                transaction.setDescription("Transaction for quotation: " + transaction.getReference());
                            }
                        } else {
                            log.debug("No reference found for transaction: {}", transaction.getTransactionId());
                            transaction.setDescription("General transaction");
                        }
                        
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
