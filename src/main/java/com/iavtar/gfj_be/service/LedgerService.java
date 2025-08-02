package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.model.request.CreateLedgerTransactionRequest;
import com.iavtar.gfj_be.model.request.UpdateLedgerTransactionRequest;
import com.iavtar.gfj_be.model.response.PagedLedgerTransactionWithClientResponse;
import org.springframework.http.ResponseEntity;

public interface LedgerService {
    ResponseEntity<?> createLedgerTransaction(CreateLedgerTransactionRequest request);
    ResponseEntity<PagedLedgerTransactionWithClientResponse> getAllLedgerTransactions(int offset, int size);
    ResponseEntity<?> updateLedgerTransaction(String transactionId, UpdateLedgerTransactionRequest request);
    ResponseEntity<?> deleteLedgerTransaction(String transactionId);
}
