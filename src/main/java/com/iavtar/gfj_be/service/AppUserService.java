package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.AppUser;
import com.iavtar.gfj_be.entity.DashboardTab;
import com.iavtar.gfj_be.entity.Role;
import com.iavtar.gfj_be.entity.enums.DashboardTabs;
import com.iavtar.gfj_be.entity.enums.RoleType;
import com.iavtar.gfj_be.repository.AppUserRepository;
import com.iavtar.gfj_be.repository.DashboardRepository;
import com.iavtar.gfj_be.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existsByUsername(String username) {
        return appUserRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return appUserRepository.findAll().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return appUserRepository.findAll().stream().anyMatch(user -> user.getPhoneNumber().equalsIgnoreCase(phoneNumber));
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findAll().stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    @Transactional
    public AppUser createBusinessAdmin(String username, String firstName, String lastName, String password, String email, String phoneNumber) {
        try {

            if (existsByUsername(username)) {
                throw new IllegalArgumentException("User with username already exists: " + username);
            }

            if (existsByEmail(email)) {
                throw new IllegalArgumentException("User with email already exists: " + email);
            }

            if (existsByPhoneNumber(phoneNumber)) {
                throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
            }

            Role businessAdminRole = roleRepository.findByName(RoleType.BUSINESS_ADMIN)
                    .orElseThrow(() -> new IllegalStateException("BUSINESS_ADMIN role not found in database"));

            Set<DashboardTab> businessAdminDashboardTabs = getDashboardTabsForBusinessAdmin();

            AppUser businessAdmin = AppUser.builder().username(username).firstName(firstName).lastName(lastName)
                    .password(passwordEncoder.encode(password)).email(email).phoneNumber(phoneNumber).isActive(true).roles(new HashSet<>())
                    .dashboardTabs(new HashSet<>()).build();

            businessAdmin.addRole(businessAdminRole);

            for (DashboardTab tab : businessAdminDashboardTabs) {
                businessAdmin.addDashboardTab(tab);
            }

            AppUser savedUser = appUserRepository.save(businessAdmin);
            System.out.println("Successfully created business admin: " + savedUser.getUsername());
            return savedUser;

        } catch (Exception e) {
            System.err.println("Error creating Business Admin: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private Set<DashboardTab> getDashboardTabsForBusinessAdmin() {
        Set<DashboardTab> tabs = new HashSet<>();

        List<DashboardTabs> requiredTabs = Arrays.asList(DashboardTabs.AGENT_ADMINISTRATION, DashboardTabs.CLIENT_ADMINISTRATION,
                DashboardTabs.CALCULATOR, DashboardTabs.LEDGER, DashboardTabs.SHIPPING, DashboardTabs.ANALYTICS, DashboardTabs.QUOTATION);

        for (DashboardTabs tabName : requiredTabs) {
            Optional<DashboardTab> tabOptional = dashboardRepository.findByName(tabName);
            if (tabOptional.isPresent()) {
                tabs.add(tabOptional.get());
            } else {
                System.err.println("Warning: Dashboard tab not found: " + tabName);
            }
        }

        return tabs;
    }

    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }
}