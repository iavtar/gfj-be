package com.iavtar.gfj_be.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUpdateShippingItemRequest {

    private String shippingId;

    private String trackingId;

    private String quotationId;

}
