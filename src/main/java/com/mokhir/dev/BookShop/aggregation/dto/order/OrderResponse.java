package com.mokhir.dev.BookShop.aggregation.dto.order;

import com.mokhir.dev.BookShop.aggregation.dto.order.details.OrderDetailsResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long totalAmount;
    private Long totalPrice;
    private Boolean status;
    private List<OrderDetailsResponse> orderDetails;
}
