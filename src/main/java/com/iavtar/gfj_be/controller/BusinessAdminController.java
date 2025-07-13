package com.iavtar.gfj_be.controller;

import com.iavtar.gfj_be.entity.AppUser;
import com.iavtar.gfj_be.model.request.CreateUserRequest;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.service.AppUserService;
import com.iavtar.gfj_be.service.BussinessAdminService;
import com.iavtar.gfj_be.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/businessAdmin")
public class BusinessAdminController {

    @Autowired
    private BussinessAdminService bussinessAdminService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private CommonUtil commonUtil;

    @PostMapping("/user")
    public ResponseEntity<?> createAgent(@RequestBody CreateUserRequest request) {
        try {

            log.info("Creating new agent with username: {}", request.getUsername());

            if (commonUtil.existsByUsername(request.getUsername())) {
                log.error("Validation failed - username already exists: {}", request.getUsername());
                ServiceResponse errorResponse = ServiceResponse.builder().message("username already exists").build();
                return ResponseEntity.internalServerError().body(errorResponse);
            }

            if (commonUtil.existsByEmail(request.getEmail())) {
                log.error("Validation failed - email already exists: {}", request.getEmail());
                ServiceResponse errorResponse = ServiceResponse.builder().message("email already exists").build();
                return ResponseEntity.internalServerError().body(errorResponse);
            }

            if (commonUtil.existsByPhoneNumber(request.getPhoneNumber())) {
                log.error("Validation failed - phone number already exists: {}", request.getPhoneNumber());
                ServiceResponse errorResponse = ServiceResponse.builder().message("Phone Number already exists").build();
                return ResponseEntity.internalServerError().body(errorResponse);
            }

            // Create the business admin user
            bussinessAdminService.createAgent(request.getUsername(), request.getFirstName(), request.getLastName(),
                    request.getPassword(), request.getEmail(), request.getPhoneNumber());

            ServiceResponse response = ServiceResponse.builder().message("Agent Created Successfully").build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error creating Agent: ").build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getAgent(@RequestParam("username") String username) {
        try{
            AppUser appUser = bussinessAdminService.getAgent(username);
            return ResponseEntity.ok(appUser);
        } catch (Exception e){
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error getting Agent: " + username).build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<?> getAllAgent() {
        try {
            List<AppUser> allUsers = bussinessAdminService.getAgents();
            return ResponseEntity.ok(allUsers);
        } catch (Exception e){
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error getting all Agents").build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/blockUser")
    public ResponseEntity<?> blockUser(@RequestParam("username") String username) {
        try {
            appUserService.blockUser(username);

            ServiceResponse response = ServiceResponse.builder()
                    .message("User blocked successfully")
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error blocking user: " + e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/unblockUser")
    public ResponseEntity<?> unblockUser(@RequestParam("username") String username) {
        try {
            appUserService.unblockUser(username);

            ServiceResponse response = ServiceResponse.builder()
                    .message("User unblocked successfully")
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error unblocking user: " + e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
