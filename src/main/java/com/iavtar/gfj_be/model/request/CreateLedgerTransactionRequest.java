package com.iavtar.gfj_be.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateLedgerTransactionRequest {

    @NotNull(message = "Client Id can not be null")
    private Long clientId;

    @NotNull(message = "amount can not be null")
    private BigDecimal amount;

    @NotNull(message = "payment method can not be null")
    private String paymentMethod;

    @NotNull(message = "order id can not be null")
    private String orderId;

    @NotNull(message = "transaction type can not be blank")
    private String transactionType;

    @NotNull(message = "category can not be null")
    private String category;

    @NotNull(message = "reference can not be null")
    private String reference;

    @NotNull(message = "payment status can not be null")
    private String paymentStatus;

    private String note;

}
