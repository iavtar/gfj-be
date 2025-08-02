package com.iavtar.gfj_be.model.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PagedLedgerTransactionWithClientResponse {

    private List<LedgerTransactionWithClientResponse> data;
    private int offset;
    private int size;
    private long totalRecords;
    private Integer nextOffset;
    private boolean hasMore;

    public static PagedLedgerTransactionWithClientResponse from(Page<LedgerTransactionWithClientResponse> page, int requestedOffset, int requestedSize) {
        long totalRecords = page.getTotalElements();
        boolean hasMore = (requestedOffset + requestedSize) < totalRecords;
        Integer nextOffset = hasMore ? requestedOffset + requestedSize : null;

        return PagedLedgerTransactionWithClientResponse.builder()
                .data(page.getContent())
                .offset(requestedOffset)
                .size(requestedSize)
                .totalRecords(totalRecords)
                .nextOffset(nextOffset)
                .hasMore(hasMore)
                .build();
    }
} 