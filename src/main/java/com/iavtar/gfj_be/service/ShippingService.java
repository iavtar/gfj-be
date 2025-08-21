package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.model.request.AddUpdateShippingItemRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShippingService {
    ResponseEntity<?> markQuotationsForShipping(List<String> quotations);

    ResponseEntity<?> getAllShipping(int offset, int size);

    ResponseEntity<?> addTrackingId(String shippingId, String trackingId);
}
