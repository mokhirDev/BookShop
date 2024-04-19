package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.dto.order.OrderRequest;
import com.mokhir.dev.BookShop.aggregation.dto.order.OrderResponse;
import com.mokhir.dev.BookShop.aggregation.dto.order.details.OrderDetailsResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.entity.Order;
import com.mokhir.dev.BookShop.aggregation.entity.OrderDetails;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.CartRepository;
import com.mokhir.dev.BookShop.repository.interfaces.OrderRepository;
import com.mokhir.dev.BookShop.service.interfaces.OrderServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServiceInterface {
    private final OrderRepository repository;
    private final CartService cartService;
    private final JwtProvider jwtProvider;
    private final CartRepository cartRepository;
    private final OrderDetailsService orderDetailsService;
    private final OrderRepository orderRepository;

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
                    .map(detail->detail.getQuantity()*detail.getPrice())
                    .reduce(0, Integer::sum)
                    .longValue();
            Order build = Order.builder()
                    .totalAmount(totalAmount)
                    .status(true)
                    .totalPrice(totalPrice)
                    .build();
            Order save = orderRepository.save(build);
            orderDetails.forEach(orderDetail -> orderDetail.setOrder(save));
            List<OrderDetailsResponse> orderDetailsResponseList =
                    orderDetailsService.updateOrderDetails(orderDetails, cartList);
            return OrderResponse.builder()
                    .id(save.getId())
                    .status(true)
                    .totalAmount(save.getTotalAmount())
                    .totalPrice(save.getTotalPrice())
                    .orderDetails(orderDetailsResponseList)
                    .build();
        } catch (NullPointerException ex) {
            throw new NullPointerException(ex.getMessage());
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private List<Cart> getUserExistsCarts(List<Long> cartIds) {
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
    }
}
