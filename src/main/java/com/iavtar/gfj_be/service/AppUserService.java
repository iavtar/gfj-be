package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.AppUser;
import com.iavtar.gfj_be.entity.DashboardTab;
import com.iavtar.gfj_be.entity.Role;
import com.iavtar.gfj_be.entity.enums.DashboardTabs;
import com.iavtar.gfj_be.entity.enums.RoleType;
import com.iavtar.gfj_be.repository.AppUserRepository;
import com.iavtar.gfj_be.repository.DashboardRepository;
import com.iavtar.gfj_be.repository.RoleRepository;
import com.iavtar.gfj_be.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommonUtil commonUtil;

    @Transactional
    public void createBusinessAdmin(String username, String firstName, String lastName, String password, String email, String phoneNumber) {
        try {
            Role businessAdminRole = roleRepository.findByName(RoleType.BUSINESS_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("BUSINESS_ADMIN role not found in database"));

            Set<DashboardTab> businessAdminDashboardTabs = commonUtil.getDashboardTabsForBusinessAdmin();
            AppUser savedUser = appUserRepository.save(commonUtil.addRoleAndDashboardTabs(
                    AppUser.builder().username(username).firstName(firstName).lastName(lastName).password(passwordEncoder.encode(password))
                            .email(email).phoneNumber(phoneNumber).isActive(true).roles(new HashSet<>()).dashboardTabs(new HashSet<>()).build(),
                    businessAdminRole, businessAdminDashboardTabs));
            log.info("Successfully created business admin: {} with ID: {}", savedUser.getUsername(), savedUser.getId());

        } catch (Exception e) {
            log.error("Error creating business admin {}: {}", username, e.getMessage());
            throw e;
        }
    }

    public AppUser getBusinessAdmin(String username) {
        return commonUtil.findByUsername(username).orElse(null);
    }

    public List<AppUser> getBusinessAdmins() {
        return commonUtil.findBusinessAdmins();
    }

    @Transactional
    public void blockUser(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        if (!user.getIsActive()) {
            throw new IllegalStateException("User is already blocked");
        }

        user.setIsActive(false);
        appUserRepository.save(user);
    }

    @Transactional
    public void unblockUser(String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        if (user.getIsActive()) {
            throw new IllegalStateException("User is already active");
        }

        user.setIsActive(true);
        appUserRepository.save(user);
    }
}