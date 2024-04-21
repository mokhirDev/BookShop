package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.order.OrderRequest;
import com.mokhir.dev.BookShop.aggregation.dto.order.OrderResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.entity.Order;
import com.mokhir.dev.BookShop.aggregation.entity.OrderDetails;
import com.mokhir.dev.BookShop.aggregation.mapper.OrderMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.CartRepository;
import com.mokhir.dev.BookShop.repository.interfaces.OrderRepository;
import com.mokhir.dev.BookShop.service.interfaces.OrderServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServiceInterface {
    private final CartService cartService;
    private final JwtProvider jwtProvider;
    private final CartRepository cartRepository;
    private final OrderDetailsService orderDetailsService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    /**
     * Creates an order based on the provided request by retrieving existing carts, creating order details,
     * calculating total amount and total price, and updating order details accordingly.
     *
     * @param request The order request containing cart IDs
     * @return The created order response
     */
    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        try {
            // Retrieve existing carts associated with the provided cart IDs
            List<Cart> cartList = getUserExistsCarts(request.getCartIds());
            // Create order details from the retrieved carts
            List<OrderDetails> orderDetails = orderDetailsService.create(cartList);
            // Calculate total amount and total price from the order details
            Long totalAmount = orderDetails.stream()
                    .map(OrderDetails::getQuantity)
                    .reduce(0, Integer::sum)
                    .longValue();
            Long totalPrice = orderDetails.stream()
                    .map(detail -> detail.getQuantity() * detail.getPrice())
                    .reduce(0, Integer::sum)
                    .longValue();
            // Build and save the order
            Order build = Order.builder()
                    .totalAmount(totalAmount)
                    .status(true)
                    .totalPrice(totalPrice)
                    .build();
            Order save = orderRepository.save(build);
            // Associate order details with the created order
            orderDetails.forEach(orderDetail -> orderDetail.setOrder(save));
            // Update order details and associated carts
            orderDetailsService.updateOrderDetails(orderDetails, cartList);
            return orderMapper.toDto(save);
        } catch (NullPointerException ex) {
            // Log null pointer exceptions and rethrow them
            logger.error("Null pointer exception occurred: {}", ex.getMessage());
            throw new NullPointerException(ex.getMessage());
        } catch (Exception e) {
            // Log database exceptions and rethrow them
            logger.error("Error creating order: {}", e.getMessage());
            throw new DatabaseException("OrderService: createOrder: " + e.getMessage());
        }
    }

    /**
     * Retrieves existing carts belonging to the current user by their IDs and removes them from the cart service.
     *
     * @param cartIds The IDs of the carts to retrieve
     * @return The list of existing carts
     */
    private List<Cart> getUserExistsCarts(List<Long> cartIds) {

        try {
            // Get the current user's username
            String currentUser = jwtProvider.getCurrentUser();
            // Check if the cart IDs list is empty
            if (cartIds.isEmpty()) {
                throw new NullPointerException("Cart IDs are empty");
            }

            // Retrieve existing carts belonging to the current user and remove them from the cart service
            List<Cart> existCarts = cartIds.stream()
                    .map(id -> cartRepository.findById(id).get())
                    .filter(cart -> cart.getCreatedBy().equals(currentUser))
                    .toList();
            // Get the IDs of the existing carts
            List<Long> existCartIds = existCarts.stream().map(Cart::getId).toList();
            // Remove existing carts from the cart service
            cartService.removeFromCart(existCartIds);
            return existCarts;
        } catch (Exception ex) {
            // Log any database exceptions that occur
            logger.error("Error retrieving user's existing carts: {}", ex.getMessage());
            // Wrap and rethrow database exceptions for higher level handling
            throw new DatabaseException("OrderService: createOrder: " + ex.getMessage());
        }
    }

    /**
     * Retrieves the last order created by the current user.
     *
     * @return The last order response
     */
    public OrderResponse getLast() {
        try {
            // Retrieve the current user's orders
            String currentUser = jwtProvider.getCurrentUser();
            List<Order> byCreatedBy = orderRepository.findByCreatedBy(currentUser);
            // Sort orders by creation time
            List<Order> sortedOrders = byCreatedBy.stream().sorted().toList();
            // Get the last order
            Order lastOrder = sortedOrders.get(sortedOrders.size() - 1);
            // Map the last order to an order response
            return orderMapper.toDto(lastOrder);
        } catch (Exception ex) {
            // Log any database exceptions that occur
            logger.error("Error retrieving last order: {}", ex.getMessage());
            // Wrap and rethrow database exceptions for higher level handling
            throw new DatabaseException("OrderService: getLast: " + ex.getMessage());
        }
    }

    /**
     * Retrieves a page of orders created by the current user.
     *
     * @param page The page number
     * @param size The page size
     * @return A page of order responses
     */
    public Page<OrderResponse> getAll(int page, int size) {
        try {
            // Retrieve orders created by the current user
            List<Order> orders = orderRepository.findByCreatedBy(jwtProvider.getCurrentUser());
            // Map orders to order responses
            List<OrderResponse> list = orders.stream().map(orderMapper::toDto).toList();
            // Return a page of order responses
            return new PageImpl<>(list, PageRequest.of(page, size), list.size());
        } catch (Exception ex) {
            // Log any database exceptions that occur
            logger.error("Error retrieving all orders: {}", ex.getMessage());
            // Wrap and rethrow database exceptions for higher level handling
            throw new DatabaseException("OrderService: getAll: " + ex.getMessage());
        }
    }
}
