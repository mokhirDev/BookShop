package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.order.details.OrderDetailsResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.entity.OrderDetails;
import com.mokhir.dev.BookShop.aggregation.mapper.BookMapper;
import com.mokhir.dev.BookShop.aggregation.mapper.CartMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.repository.interfaces.OrderDetailsRepository;
import com.mokhir.dev.BookShop.repository.interfaces.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailsService {
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;
    private final CartMapper cartMapper;
    private final BookMapper bookMapper;

    public List<OrderDetails> create(List<Cart> orders) {
        try {
            List<OrderDetails> detailsList = orders.stream().map(cartMapper::toOrderDetails).toList();
            return orderDetailsRepository.saveAll(detailsList);
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    public List<OrderDetailsResponse> updateOrder(List<OrderDetails> orderDetails) {
        List<OrderDetails> saveAll = orderDetailsRepository.saveAll(orderDetails);
        return saveAll.stream().map(detail -> {
            return OrderDetailsResponse.builder()
                    .orderId(detail.getOrder().getId())
                    .book(bookMapper.toDto(detail.getBook()))
                    .quantity(detail.getQuantity())
                    .price(detail.getPrice())
                    .build();
        }).toList();
    }
}
