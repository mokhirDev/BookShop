package com.mokhir.dev.BookShop.service.interfaces;

import com.mokhir.dev.BookShop.aggregation.dto.order.OrderRequest;
import com.mokhir.dev.BookShop.aggregation.dto.order.OrderResponse;

public interface OrderServiceInterface {
    OrderResponse createOrder(OrderRequest orderRequest);
}
