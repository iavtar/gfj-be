package com.iavtar.gfj_be.model.response;

import com.iavtar.gfj_be.entity.Client;
import com.iavtar.gfj_be.entity.LedgerTransaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class LedgerTransactionWithClientResponse {

    // Ledger Transaction fields
    private Long id;
    private String transactionId;
    private Long clientId;
    private BigDecimal amount;
    private String paymentMethod;
    private String orderId;
    private String category;
    private String reference;
    private Date createdAt;
    private Date updatedAt;
    private String paymentStatus;
    private String note;
    private String description;

    // Client fields
    private String clientName;
    private String email;
    private String phoneNumber;
    private String businessAddress;
    private String city;
    private String state;
    private String country;

    public static LedgerTransactionWithClientResponse from(LedgerTransaction transaction, Client client) {
        return LedgerTransactionWithClientResponse.builder()
                .id(transaction.getId())
                .transactionId(transaction.getTransactionId())
                .clientId(transaction.getClientId())
                .amount(transaction.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .orderId(transaction.getOrderId())
                .category(transaction.getCategory())
                .reference(transaction.getReference())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .paymentStatus(transaction.getPaymentStatus())
                .note(transaction.getNote())
                .description(transaction.getDescription())
                .clientName(client != null ? client.getClientName() : null)
                .email(client != null ? client.getEmail() : null)
                .phoneNumber(client != null ? client.getPhoneNumber() : null)
                .businessAddress(client != null ? client.getBusinessAddress() : null)
                .city(client != null ? client.getCity() : null)
                .state(client != null ? client.getState() : null)
                .country(client != null ? client.getCountry() : null)
                .build();
    }
} 