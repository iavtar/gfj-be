package com.iavtar.gfj_be.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long   id;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    boolean isActive;
}