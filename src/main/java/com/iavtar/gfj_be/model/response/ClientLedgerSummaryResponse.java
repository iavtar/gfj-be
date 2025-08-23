package com.iavtar.gfj_be.model.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ClientLedgerSummaryResponse {

    private Long clientId;
    private String clientName;
    private BigDecimal currentBalance;
    private Long totalTransactions;
    private List<ClientLedgerResponse> recentTransactions;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
