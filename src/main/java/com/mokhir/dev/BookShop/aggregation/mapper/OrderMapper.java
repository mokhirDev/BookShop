package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.order.OrderResponse;
import com.mokhir.dev.BookShop.aggregation.dto.order.details.OrderDetailsResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Order;
import com.mokhir.dev.BookShop.aggregation.entity.OrderDetails;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.BaseMapper;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.OrderDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class OrderMapper implements BaseMapper<Order, OrderResponse> {
    private final OrderDetailsRepository orderDetailsRepository;
    private final OrderDetailsMapper orderDMapper;
    private final JwtProvider jwtProvider;

    @Override
    public OrderResponse toDto(Order save) {
        List<OrderDetails> saveAll = orderDetailsRepository.findAllByCreatedBy(jwtProvider.getCurrentUser());
        List<OrderDetails> detailsList = saveAll
                .stream()
                .filter(orderDetails ->
                        orderDetails.getOrder().getId().equals(save.getId()))
                .toList();
        List<OrderDetailsResponse> orderDetailsResponseList = detailsList
                .stream()
                .map(orderDMapper::toDto)
                .toList();
        return OrderResponse.builder()
                .id(save.getId())
                .status(true)
                .totalAmount(save.getTotalAmount())
                .totalPrice(save.getTotalPrice())
                .orderDetails(orderDetailsResponseList)
                .build();
    }
}
