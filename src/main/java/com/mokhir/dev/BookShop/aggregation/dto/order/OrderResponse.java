package com.mokhir.dev.BookShop.aggregation.dto.order;

import com.mokhir.dev.BookShop.aggregation.dto.order.details.OrderDetailsResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private Integer totalAmount;
    private Boolean status;
    private List<OrderDetailsResponse> orderDetails;
}
