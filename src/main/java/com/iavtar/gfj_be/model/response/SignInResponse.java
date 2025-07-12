package com.iavtar.gfj_be.model.response;

import com.iavtar.gfj_be.entity.Dashboard;
import com.iavtar.gfj_be.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
public class SignInResponse {

    private Long id;

    private String username;

    private String name;

    private String email;

    private String phoneNumber;

    private Set<String> roles;

    private Set<String> dashboards;

    private String token;

}
