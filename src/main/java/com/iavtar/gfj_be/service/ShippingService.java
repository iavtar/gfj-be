package com.iavtar.gfj_be.service;

import com.iavtar.gfj_be.model.request.AddUpdateShippingItemRequest;
import org.springframework.http.ResponseEntity;

public interface ShippingService {
    ResponseEntity<?> addShippingItem(AddUpdateShippingItemRequest request);

    ResponseEntity<?> updateShippingItem(String shippingId, AddUpdateShippingItemRequest request);

    ResponseEntity<?> deleteShippingItem(String shippingId);

    ResponseEntity<?> getAllShippingItems();
}
