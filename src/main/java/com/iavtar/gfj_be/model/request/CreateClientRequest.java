package com.iavtar.gfj_be.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClientRequest {

    private String     clientName;
    private String     clientLocation;
    private String     businessLogoUrl;
    private String     email;
    private String     phoneNumber;
    private String     businessAddress;
    private String     shippingAddress;
    private String     city;
    private String     state;
    private String     country;
    private String     zipCode;
    private String     einNumber;
    private String     taxId;
    private BigDecimal diamondSettingPrice;
    private BigDecimal goldWastagePercentage;
    private BigDecimal profitAndLabourPercentage;
    private BigDecimal cadCamWaxPrice;
    private Long       agentId;

}
