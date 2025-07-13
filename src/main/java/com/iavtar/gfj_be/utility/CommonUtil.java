package com.iavtar.gfj_be.utility;

import com.iavtar.gfj_be.entity.AppUser;
import com.iavtar.gfj_be.entity.DashboardTab;
import com.iavtar.gfj_be.entity.enums.DashboardTabs;
import com.iavtar.gfj_be.entity.enums.RoleType;
import com.iavtar.gfj_be.repository.AppUserRepository;
import com.iavtar.gfj_be.repository.DashboardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CommonUtil {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    public boolean existsByUsername(String username) {
        log.debug("Checking if username exists: {}", username);
        return appUserRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);
        return appUserRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        log.debug("Checking if phone number exists: {}", phoneNumber);
        return appUserRepository.findAll().stream()
                .anyMatch(user -> user.getPhoneNumber().equalsIgnoreCase(phoneNumber));
    }

    public Optional<AppUser> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return appUserRepository.findByUsername(username);
    }

    public Optional<AppUser> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return appUserRepository.findAll().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Set<DashboardTab> getDashboardTabs(List<DashboardTabs> requiredTabs, String roleName) {
        log.debug("Getting dashboard tabs for {} role", roleName);

        Set<DashboardTab> tabs = new HashSet<>();

        for (DashboardTabs tabName : requiredTabs) {
            Optional<DashboardTab> tabOptional = dashboardRepository.findByName(tabName);
            if (tabOptional.isPresent()) {
                tabs.add(tabOptional.get());
                log.debug("Added dashboard tab: {} for {}", tabName, roleName);
            } else {
                log.warn("Dashboard tab not found in database: {} for {}", tabName, roleName);
            }
        }

        log.debug("Retrieved {} dashboard tabs for {}", tabs.size(), roleName);
        return tabs;
    }

    public Set<DashboardTab> getDashboardTabsForAgent() {
        List<DashboardTabs> requiredTabs = Arrays.asList(
                DashboardTabs.CLIENT_ADMINISTRATION,
                DashboardTabs.CALCULATOR,
                DashboardTabs.QUOTATION
        );
        return getDashboardTabs(requiredTabs, "agent");
    }

    public Set<DashboardTab> getDashboardTabsForBusinessAdmin() {
        List<DashboardTabs> requiredTabs = Arrays.asList(
                DashboardTabs.AGENT_ADMINISTRATION,
                DashboardTabs.CLIENT_ADMINISTRATION,
                DashboardTabs.CALCULATOR,
                DashboardTabs.LEDGER,
                DashboardTabs.SHIPPING,
                DashboardTabs.ANALYTICS,
                DashboardTabs.QUOTATION
        );
        return getDashboardTabs(requiredTabs, "business admin");
    }

    public AppUser addRoleAndDashboardTabs(AppUser user, com.iavtar.gfj_be.entity.Role role, Set<DashboardTab> dashboardTabs) {
        log.debug("Adding role {} and {} dashboard tabs to user: {}",
                role.getName(), dashboardTabs.size(), user.getUsername());

        user.addRole(role);

        for (DashboardTab tab : dashboardTabs) {
            user.addDashboardTab(tab);
        }

        log.debug("Successfully added role and dashboard tabs to user: {}", user.getUsername());
        return user;
    }

    public List<AppUser> findUsersByRole(RoleType roleType) {
        log.debug("Finding users by role: {}", roleType);
        return appUserRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals(roleType)))
                .collect(Collectors.toList());
    }

    public List<AppUser> findBusinessAdmins() {
        log.debug("Finding all business admins");
        List<AppUser> businessAdmins = findUsersByRole(RoleType.BUSINESS_ADMIN);
        log.info("Found {} business admins", businessAdmins.size());
        return businessAdmins;
    }

    public List<AppUser> findAgents() {
        log.debug("Finding all agents");
        List<AppUser> agents = findUsersByRole(RoleType.AGENT);
        log.info("Found {} agents", agents.size());
        return agents;
    }
}