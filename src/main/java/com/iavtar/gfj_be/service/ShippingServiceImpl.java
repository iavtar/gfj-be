package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.entity.Quotation;
import com.iavtar.gfj_be.entity.ShippingTracker;
import com.iavtar.gfj_be.model.request.AddUpdateShippingItemRequest;
import com.iavtar.gfj_be.model.response.ServiceResponse;
import com.iavtar.gfj_be.repository.ClientRepository;
import com.iavtar.gfj_be.repository.QuotationRepository;
import com.iavtar.gfj_be.repository.ShippingServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import com.iavtar.gfj_be.entity.Client;

@Slf4j
@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private ShippingServiceRepository shippingRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ShippingIdGeneratorService shippingIdGeneratorService;

    @Override
    public ResponseEntity<?> addShippingItem(AddUpdateShippingItemRequest request) {
        try {
            log.info("Adding shipping item for quotation: {}", request.getQuotationId());
            
            // Validate request
            if (request.getQuotationId() == null || request.getQuotationId().trim().isEmpty()) {
                log.error("Validation failed - quotation ID is required");
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Quotation ID is required")
                        .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Check if quotation exists
            Optional<Quotation> quotationOptional = quotationRepository.findByQuotationId(request.getQuotationId());
            if (quotationOptional.isEmpty()) {
                log.error("Quotation not found with ID: {}", request.getQuotationId());
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Quotation not found with ID: " + request.getQuotationId())
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Check if shipping item already exists for this quotation
            Optional<ShippingTracker> existingShipping = shippingRepository.findByQuotationId(request.getQuotationId());
            if (existingShipping.isPresent()) {
                log.error("Shipping item already exists for quotation: {}", request.getQuotationId());
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping item already exists for this quotation")
                        .build();
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }

            // Create shipping tracker
            ShippingTracker shippingTracker = ShippingTracker.builder()
                    .shippingId(shippingIdGeneratorService.generateId())
                    .trackingId(request.getTrackingId())
                    .quotationId(request.getQuotationId())
                    .status("PENDING")
                    .build();

            ShippingTracker savedShipping = shippingRepository.save(shippingTracker);
            log.info("Shipping item created successfully with ID: {}", savedShipping.getShippingId());

            ServiceResponse response = ServiceResponse.builder()
                    .message("Shipping item created successfully")
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error adding shipping item: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error adding shipping item: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> updateShippingItem(String shippingId, AddUpdateShippingItemRequest request) {
        try {
            log.info("Updating shipping item with ID: {}", shippingId);
            
            // Validate request
            if (shippingId == null || shippingId.trim().isEmpty()) {
                log.error("Validation failed - shipping ID is required");
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping ID is required")
                        .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Find existing shipping item
            Optional<ShippingTracker> existingShippingOptional = shippingRepository.findByShippingId(shippingId);
            if (existingShippingOptional.isEmpty()) {
                log.error("Shipping item not found with ID: {}", shippingId);
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping item not found with ID: " + shippingId)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            ShippingTracker existingShipping = existingShippingOptional.get();

            // Update fields if provided
            if (request.getTrackingId() != null && !request.getTrackingId().trim().isEmpty()) {
                existingShipping.setTrackingId(request.getTrackingId());
            }

            if (request.getQuotationId() != null && !request.getQuotationId().trim().isEmpty()) {
                // Check if quotation exists
                Optional<Quotation> quotationOptional = quotationRepository.findByQuotationId(request.getQuotationId());
                if (quotationOptional.isEmpty()) {
                    log.error("Quotation not found with ID: {}", request.getQuotationId());
                    ServiceResponse errorResponse = ServiceResponse.builder()
                            .message("Quotation not found with ID: " + request.getQuotationId())
                            .build();
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }
                existingShipping.setQuotationId(request.getQuotationId());
            }

            ShippingTracker updatedShipping = shippingRepository.save(existingShipping);
            log.info("Shipping item updated successfully with ID: {}", updatedShipping.getShippingId());

            ServiceResponse response = ServiceResponse.builder()
                    .message("Shipping item updated successfully")
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating shipping item: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error updating shipping item: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> deleteShippingItem(String shippingId) {
        try {
            log.info("Deleting shipping item with ID: {}", shippingId);
            
            // Validate request
            if (shippingId == null || shippingId.trim().isEmpty()) {
                log.error("Validation failed - shipping ID is required");
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping ID is required")
                        .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Find existing shipping item
            Optional<ShippingTracker> existingShippingOptional = shippingRepository.findByShippingId(shippingId);
            if (existingShippingOptional.isEmpty()) {
                log.error("Shipping item not found with ID: {}", shippingId);
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping item not found with ID: " + shippingId)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            shippingRepository.delete(existingShippingOptional.get());
            log.info("Shipping item deleted successfully with ID: {}", shippingId);

            ServiceResponse response = ServiceResponse.builder()
                    .message("Shipping item deleted successfully")
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error deleting shipping item: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error deleting shipping item: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<?> getAllShippingItems() {
        try {
            log.info("Getting all shipping items with client details");
            
            List<ShippingTracker> shippingItems = shippingRepository.findAll();
            log.info("Found {} shipping items", shippingItems.size());

            // Create response with client details
            List<Map<String, Object>> shippingItemsWithClientDetails = shippingItems.stream()
                    .map(shippingItem -> {
                        Map<String, Object> itemWithClient = new HashMap<>();
                        
                        // Add shipping item details
                        itemWithClient.put("id", shippingItem.getId());
                        itemWithClient.put("shippingId", shippingItem.getShippingId());
                        itemWithClient.put("trackingId", shippingItem.getTrackingId());
                        itemWithClient.put("quotationId", shippingItem.getQuotationId());
                        itemWithClient.put("status", shippingItem.getStatus());
                        itemWithClient.put("createdAt", shippingItem.getCreatedAt());
                        itemWithClient.put("updatedAt", shippingItem.getUpdatedAt());
                        
                        // Find quotation and client details
                        if (shippingItem.getQuotationId() != null) {
                            Optional<Quotation> quotationOptional = quotationRepository.findByQuotationId(shippingItem.getQuotationId());
                            if (quotationOptional.isPresent()) {
                                Quotation quotation = quotationOptional.get();
                                
                                // Add quotation details
                                Map<String, Object> quotationDetails = new HashMap<>();
                                quotationDetails.put("quotationId", quotation.getQuotationId());
                                quotationDetails.put("description", quotation.getDescription());
                                quotationDetails.put("price", quotation.getPrice());
                                quotationDetails.put("quotationStatus", quotation.getQuotationStatus());
                                quotationDetails.put("createdAt", quotation.getCreatedAt());
                                itemWithClient.put("quotationDetails", quotationDetails);
                                
                                // Find client details
                                if (quotation.getClientId() != null) {
                                    Optional<Client> clientOptional = clientRepository.findById(quotation.getClientId());
                                    if (clientOptional.isPresent()) {
                                        Client client = clientOptional.get();
                                        
                                        // Add client details
                                        Map<String, Object> clientDetails = new HashMap<>();
                                        clientDetails.put("clientName", client.getClientName());
                                        clientDetails.put("phoneNumber", client.getPhoneNumber());
                                        clientDetails.put("shippingAddress", client.getShippingAddress());
                                        clientDetails.put("city", client.getCity());
                                        clientDetails.put("state", client.getState());
                                        clientDetails.put("country", client.getCountry());
                                        clientDetails.put("zipCode", client.getZipCode());
                                        itemWithClient.put("clientDetails", clientDetails);
                                    } else {
                                        itemWithClient.put("clientDetails", null);
                                        log.warn("Client not found for clientId: {}", quotation.getClientId());
                                    }
                                } else {
                                    itemWithClient.put("clientDetails", null);
                                    log.warn("No clientId found in quotation: {}", quotation.getQuotationId());
                                }
                            } else {
                                itemWithClient.put("quotationDetails", null);
                                itemWithClient.put("clientDetails", null);
                                log.warn("Quotation not found for quotationId: {}", shippingItem.getQuotationId());
                            }
                        } else {
                            itemWithClient.put("quotationDetails", null);
                            itemWithClient.put("clientDetails", null);
                            log.warn("No quotationId found in shipping item: {}", shippingItem.getShippingId());
                        }
                        
                        return itemWithClient;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(shippingItemsWithClientDetails);

        } catch (Exception e) {
            log.error("Error getting all shipping items: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error getting all shipping items: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Additional helper methods for shipping management

    public ResponseEntity<?> getShippingItemByShippingId(String shippingId) {
        try {
            log.info("Getting shipping item with ID: {}", shippingId);
            
            Optional<ShippingTracker> shippingOptional = shippingRepository.findByShippingId(shippingId);
            if (shippingOptional.isEmpty()) {
                log.error("Shipping item not found with ID: {}", shippingId);
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping item not found with ID: " + shippingId)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            return ResponseEntity.ok(shippingOptional.get());

        } catch (Exception e) {
            log.error("Error getting shipping item: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error getting shipping item: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    public ResponseEntity<?> getShippingItemByTrackingId(String trackingId) {
        try {
            log.info("Getting shipping item with tracking ID: {}", trackingId);
            
            Optional<ShippingTracker> shippingOptional = shippingRepository.findByTrackingId(trackingId);
            if (shippingOptional.isEmpty()) {
                log.error("Shipping item not found with tracking ID: {}", trackingId);
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping item not found with tracking ID: " + trackingId)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            return ResponseEntity.ok(shippingOptional.get());

        } catch (Exception e) {
            log.error("Error getting shipping item by tracking ID: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error getting shipping item by tracking ID: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    public ResponseEntity<?> getShippingItemByQuotationId(String quotationId) {
        try {
            log.info("Getting shipping item for quotation: {}", quotationId);
            
            Optional<ShippingTracker> shippingOptional = shippingRepository.findByQuotationId(quotationId);
            if (shippingOptional.isEmpty()) {
                log.error("Shipping item not found for quotation: {}", quotationId);
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping item not found for quotation: " + quotationId)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            return ResponseEntity.ok(shippingOptional.get());

        } catch (Exception e) {
            log.error("Error getting shipping item by quotation ID: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error getting shipping item by quotation ID: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    public ResponseEntity<?> updateShippingStatus(String shippingId, String status) {
        try {
            log.info("Updating shipping status for ID: {} to status: {}", shippingId, status);
            
            // Validate request
            if (shippingId == null || shippingId.trim().isEmpty()) {
                log.error("Validation failed - shipping ID is required");
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping ID is required")
                        .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (status == null || status.trim().isEmpty()) {
                log.error("Validation failed - status is required");
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Status is required")
                        .build();
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Find existing shipping item
            Optional<ShippingTracker> existingShippingOptional = shippingRepository.findByShippingId(shippingId);
            if (existingShippingOptional.isEmpty()) {
                log.error("Shipping item not found with ID: {}", shippingId);
                ServiceResponse errorResponse = ServiceResponse.builder()
                        .message("Shipping item not found with ID: " + shippingId)
                        .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            ShippingTracker existingShipping = existingShippingOptional.get();
            existingShipping.setStatus(status);

            ShippingTracker updatedShipping = shippingRepository.save(existingShipping);
            log.info("Shipping status updated successfully for ID: {}", updatedShipping.getShippingId());

            ServiceResponse response = ServiceResponse.builder()
                    .message("Shipping status updated successfully")
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating shipping status: {}", e.getMessage(), e);
            ServiceResponse errorResponse = ServiceResponse.builder()
                    .message("Error updating shipping status: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
}
