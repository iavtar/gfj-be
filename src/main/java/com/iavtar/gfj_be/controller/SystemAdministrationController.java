package com.iavtar.gfj_be.controller;

import com.iavtar.gfj_be.entity.AppUser;
import com.iavtar.gfj_be.model.request.CreateBusinessAdminRequest;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/systemAdministration")
public class SystemAdministrationController {

    @Autowired
    private AppUserService appUserService;

    @PostMapping("/createBusinessAdmin")
    public ResponseEntity<?> createBusinessAdmin(@RequestBody CreateBusinessAdminRequest request) {
        try {

            // Create the business admin user
            AppUser businessAdmin = appUserService.createBusinessAdmin(request.getUsername(), request.getFirstName(), request.getLastName(),
                    request.getPassword(), request.getEmail(), request.getPhoneNumber());

            ServiceResponse response = ServiceResponse.builder().message("Business Admin Created Successfully").build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ServiceResponse errorResponse = ServiceResponse.builder().message("Error creating business admin: " + e.getMessage()).build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

}
