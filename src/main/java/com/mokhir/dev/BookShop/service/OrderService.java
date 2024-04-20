package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.order.OrderRequest;
import com.mokhir.dev.BookShop.aggregation.dto.order.OrderResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.entity.Order;
import com.mokhir.dev.BookShop.aggregation.entity.OrderDetails;
import com.mokhir.dev.BookShop.aggregation.mapper.OrderMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.CartRepository;
import com.mokhir.dev.BookShop.repository.interfaces.OrderRepository;
import com.mokhir.dev.BookShop.service.interfaces.OrderServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServiceInterface {
    private final CartService cartService;
    private final JwtProvider jwtProvider;
    private final CartRepository cartRepository;
    private final OrderDetailsService orderDetailsService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        try {
            List<Cart> cartList = getUserExistsCarts(request.getCartIds());
            List<OrderDetails> orderDetails = orderDetailsService.create(cartList);
            Long totalAmount = orderDetails.stream()
                    .map(OrderDetails::getQuantity)
                    .reduce(0, Integer::sum)
                    .longValue();
            Long totalPrice = orderDetails.stream()
                    .map(detail -> detail.getQuantity() * detail.getPrice())
                    .reduce(0, Integer::sum)
                    .longValue();
            Order build = Order.builder()
                    .totalAmount(totalAmount)
                    .status(true)
                    .totalPrice(totalPrice)
                    .build();
            Order save = orderRepository.save(build);
            orderDetails.forEach(orderDetail -> orderDetail.setOrder(save));
            orderDetailsService.updateOrderDetails(orderDetails, cartList);
            return orderMapper.toDto(save);
        } catch (NullPointerException ex) {
            throw new NullPointerException(ex.getMessage());
        } catch (Exception e) {
            throw new DatabaseException("OrderService: createOrder: " + e.getMessage());
        }
    }

    private List<Cart> getUserExistsCarts(List<Long> cartIds) {
        try {
            String currentUser = jwtProvider.getCurrentUser();
            if (cartIds.isEmpty()) {
                throw new NullPointerException("Cart IDs are empty");
            }

            List<Cart> existCarts = cartIds.stream()
                    .map(id -> cartRepository.findById(id).get())
                    .filter(cart -> cart.getCreatedBy().equals(currentUser))
                    .toList();
            List<Long> existCartIds = existCarts.stream().map(Cart::getId).toList();
            cartService.removeFromCart(existCartIds);
            return existCarts;
        } catch (Exception ex) {
            throw new DatabaseException("OrderService: createOrder: " + ex.getMessage());
        }
    }

    public OrderResponse getLast() {
        try {
            String currentUser = jwtProvider.getCurrentUser();
            List<Order> byCreatedBy = orderRepository.findByCreatedBy(currentUser);
            List<Order> list = byCreatedBy
                    .stream()
                    .sorted().toList();
            Order last = list.get(list.size() - 1);
            return orderMapper.toDto(last);
        } catch (Exception ex) {
            throw new DatabaseException("OrderService: getLast: " + ex.getMessage());
        }
    }

    public Page<OrderResponse> getAll(int page, int size) {
        try {
            List<Order> orders = orderRepository.findByCreatedBy(jwtProvider.getCurrentUser());
            List<OrderResponse> list = orders.stream().map(orderMapper::toDto).toList();
            return new PageImpl<>(list, PageRequest.of(page, size), list.size());
        } catch (Exception ex) {
            throw new DatabaseException("OrderService: getAll: " + ex.getMessage());
        }
    }
}
