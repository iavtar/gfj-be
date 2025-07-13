package com.iavtar.gfj_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String clientName;

    @Column
    private String businessLogoUrl;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String businessAddress;

    @Column(columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 50)
    private String country;

    @Column(length = 20)
    private String zipCode;

    @Column(length = 50)
    private String einNumber;

    @Column(length = 50)
    private String taxId;

    @Column(precision = 10, scale = 2)
    private BigDecimal diamondSettingPrice;

    @Column(precision = 5, scale = 2)
    private BigDecimal goldWastagePercentage;

    @Column(precision = 5, scale = 2)
    private BigDecimal profitAndLabourPercentage;

    @Column(precision = 10, scale = 2)
    private BigDecimal cadCamWaxPrice;

    @Column(length = 100)
    private String website;

    @Column(length = 50)
    private String businessType;

    // Only map to users who have AGENT role - Simple validation in setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AppUser agent;

    // Simple validation - only allow AGENT role users
    public void setAgent(AppUser agent) {
        if (agent != null) {
            boolean hasAgentRole = agent.getRoles().stream()
                    .anyMatch(role -> role.getName() == com.iavtar.gfj_be.entity.enums.RoleType.AGENT);

            if (!hasAgentRole) {
                throw new IllegalArgumentException("Only users with AGENT role can be assigned as agent");
            }
        }
        this.agent = agent;
    }

}