package com.iavtar.gfj_be.model.response;

import com.iavtar.gfj_be.entity.ClientLedger;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ClientLedgerResponse {

    private Long id;
    private Long clientId;
    private String transactionId;
    private BigDecimal amount;
    private ClientLedger.TransactionType transactionType;
    private String description;
    private String reference;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClientLedgerResponse from(ClientLedger ledger) {
        return ClientLedgerResponse.builder()
                .id(ledger.getId())
                .clientId(ledger.getClientId())
                .transactionId(ledger.getTransactionId())
                .amount(ledger.getAmount())
                .transactionType(ledger.getTransactionType())
                .description(ledger.getDescription())
                .reference(ledger.getReference())
                .note(ledger.getNote())
                .createdAt(ledger.getCreatedAt())
                .updatedAt(ledger.getUpdatedAt())
                .build();
    }
}
