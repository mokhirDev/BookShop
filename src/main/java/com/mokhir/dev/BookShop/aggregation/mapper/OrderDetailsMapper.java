package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.order.details.OrderDetailsResponse;
import com.mokhir.dev.BookShop.aggregation.entity.OrderDetails;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderDetailsMapper implements BaseMapper<OrderDetails, OrderDetailsResponse> {
    private final BookMapper bookMapper;

    @Override
    public OrderDetailsResponse toDto(OrderDetails detail) {
        return OrderDetailsResponse.builder()
                .id(detail.getId())
                .orderId(detail.getOrder().getId())
                .book(bookMapper.toDto(detail.getBook()))
                .quantity(detail.getQuantity())
                .price(detail.getPrice())
                .totalPrice(detail.getPrice() * detail.getQuantity())
                .build();
    }
}
