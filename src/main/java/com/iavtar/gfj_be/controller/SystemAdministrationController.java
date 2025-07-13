package com.iavtar.gfj_be.controller;

import com.iavtar.gfj_be.entity.AppUser;
import com.iavtar.gfj_be.model.request.CreateUserRequest;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.model.response.UserResponse;
import com.iavtar.gfj_be.service.AppUserService;
import com.iavtar.gfj_be.utility.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/systemAdministration")
public class SystemAdministrationController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private CommonUtil commonUtil;

    @PostMapping("/user")
    public ResponseEntity<?> createBusinessAdmin(@RequestBody CreateUserRequest request) {
        try {

            log.info("Creating new business admin with username: {}", request.getUsername());

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
            appUserService.createBusinessAdmin(request.getUsername(), request.getFirstName(), request.getLastName(),
                    request.getPassword(), request.getEmail(), request.getPhoneNumber());

            ServiceResponse response = ServiceResponse.builder().message("Business Admin Created Successfully").build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error creating business admin: ").build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getUser(@RequestParam("username") String username) {
        try{
            AppUser appUser = appUserService.getBusinessAdmin(username);
            UserResponse userResponse = UserResponse.builder().id(appUser.getId()).username(appUser.getUsername()).firstName(appUser.getFirstName()).lastName(appUser.getLastName()).email(appUser.getEmail()).phoneNumber(appUser.getPhoneNumber()).build();
            return ResponseEntity.ok(appUser);
        } catch (Exception e){
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error getting : " + username).build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<?> getAllUser() {
        try {
            List<AppUser> allUsers = appUserService.getBusinessAdmins();
            List<UserResponse>userResponses = new ArrayList<>();
            for(AppUser appUser : allUsers){
                userResponses.add(UserResponse.builder().id(appUser.getId()).username(appUser.getUsername()).firstName(appUser.getFirstName()).lastName(appUser.getLastName()).email(appUser.getEmail()).phoneNumber(appUser.getPhoneNumber()).build());
            }
            return ResponseEntity.ok(userResponses);
        } catch (Exception e){
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error getting all BusinessAdmin").build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
