package com.iavtar.gfj_be.controller;

import com.iavtar.gfj_be.model.request.CreateLedgerTransactionRequest;
import com.iavtar.gfj_be.model.request.UpdateLedgerTransactionRequest;
import com.iavtar.gfj_be.model.response.PagedLedgerTransactionWithClientResponse;
import com.iavtar.gfj_be.service.LedgerService;
import com.iavtar.gfj_be.utility.ValidationUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    @Autowired
    private LedgerService ledgerService;

    @Autowired
    private ValidationUtil validationUtil;

    @PostMapping("/createTransaction")
    public ResponseEntity<?> createLedgerTransaction(@Valid @RequestBody CreateLedgerTransactionRequest request,
                                                     BindingResult result) {
        ResponseEntity<?> errorMap = validationUtil.validate(result);
        if(!(errorMap == null)) {
            return errorMap;
        }
        return ledgerService.createLedgerTransaction(request);
    }

    @GetMapping("/")
    public ResponseEntity<PagedLedgerTransactionWithClientResponse> getAllLedgerTransaction(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int size) {
        return ledgerService.getAllLedgerTransactions(offset, size);
    }

    @PutMapping("/updateTransaction/{transactionId}")
    public ResponseEntity<?> updateLedgerTransaction(@PathVariable String transactionId,
                                                     @Valid @RequestBody UpdateLedgerTransactionRequest request,
                                                     BindingResult result) {
        ResponseEntity<?> errorMap = validationUtil.validate(result);
        if(!(errorMap == null)) {
            return errorMap;
        }
        return ledgerService.updateLedgerTransaction(transactionId, request);
    }

    @DeleteMapping("/deleteTransaction/{transactionId}")
    public ResponseEntity<?> deleteLedgerTransaction(@PathVariable String transactionId) {
        return ledgerService.deleteLedgerTransaction(transactionId);
    }

}
