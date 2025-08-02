package com.iavtar.gfj_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@Entity
@Table(name = "ledger_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Lob
    @Column(columnDefinition = "TEXT")
    private String note;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

}
