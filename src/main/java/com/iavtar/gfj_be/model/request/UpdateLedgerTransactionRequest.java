package com.iavtar.gfj_be.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateLedgerTransactionRequest {

    private String transactionType;

    private String category;

    private BigDecimal amount;

    private String paymentMethod;

    private String orderId;

    private String reference;

    private String paymentStatus;

    private String note;

} 