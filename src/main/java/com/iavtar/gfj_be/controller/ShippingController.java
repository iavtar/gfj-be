package com.iavtar.gfj_be.controller;

import com.iavtar.gfj_be.model.request.AddUpdateShippingItemRequest;
import com.iavtar.gfj_be.service.ShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @PostMapping("/addShippingItem")
    public ResponseEntity<?> addShippingItem(@RequestBody AddUpdateShippingItemRequest request) {
        return shippingService.addShippingItem(request);
    }

    @PutMapping("updateShippingItem/{shippingId}")
    public ResponseEntity<?> updateShippingItem(@PathVariable String shippingId,
                                                @RequestBody AddUpdateShippingItemRequest request) {
        return shippingService.updateShippingItem(shippingId, request);
    }

    @DeleteMapping("deleteShippingItem/{shippingId}")
    public ResponseEntity<?> deleteShippingItem(@PathVariable String shippingId) {
        return shippingService.deleteShippingItem(shippingId);
    }

    @GetMapping("/getAllShippingItems")
    public ResponseEntity<?> getAllShippingItems() {
        return shippingService.getAllShippingItems();
    }

}
