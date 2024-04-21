package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.dto.order.details.OrderDetailsResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.entity.OrderDetails;
import com.mokhir.dev.BookShop.aggregation.mapper.BookMapper;
import com.mokhir.dev.BookShop.aggregation.mapper.CartMapper;
import com.mokhir.dev.BookShop.aggregation.mapper.OrderDetailsMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.OrderDetailsRepository;
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
public class OrderDetailsService {
    private final OrderDetailsRepository orderDetailsRepository;
    private final CartMapper cartMapper;
    private final BookMapper bookMapper;
    private final BookService bookService;
    private final JwtProvider jwtProvider;
    private final OrderDetailsMapper orderDetailsMapper;
    private static final Logger logger = LoggerFactory.getLogger(OrderDetailsService.class);


    /**
     * Service method to create order details from a list of carts.
     *
     * @param carts The list of carts to create order details from
     * @return A list of order details created from the carts
     */
    public List<OrderDetails> create(List<Cart> carts) {
        try {
            // Map each cart to order details using cartMapper and save them
            List<OrderDetails> detailsList = carts.stream().map(cartMapper::toOrderDetails).toList();
            return orderDetailsRepository.saveAll(detailsList);
        } catch (Exception ex) {
            // Log any database exceptions that occur
            logger.error("Error creating order details: {}", ex.getMessage());
            // Wrap and rethrow database exceptions for higher level handling
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Service method to retrieve all order details for the current user.
     *
     * @param page The page number
     * @param size The size of each page
     * @return A page of order details for the current user
     */
    public Page<OrderDetailsResponse> getAll(int page, int size) {
        try {
            // Retrieve all order details for the current user
            List<OrderDetails> allByCreatedBy = orderDetailsRepository.findAllByCreatedBy(jwtProvider.getCurrentUser());
            // Map the order details to order details response DTOs
            List<OrderDetailsResponse> list = allByCreatedBy.stream().map(orderDetailsMapper::toDto).toList();
            // Create and return a page of order details response DTOs
            return new PageImpl<>(list, PageRequest.of(page, size), list.size());
        } catch (Exception e) {
            // Log any database exceptions that occur
            logger.error("Error retrieving all order details: {}", e.getMessage());
            // Wrap and rethrow database exceptions for higher level handling
            throw new DatabaseException("OrderDetailsService: getAll: " + e.getMessage());
        }
    }

    /**
     * Service method to update order details based on the provided order details and cart list.
     *
     * @param orderDetails The list of order details to be updated
     * @param cartList     The list of carts
     * @return A list of updated order details response DTOs
     */
    public List<OrderDetailsResponse> updateOrderDetails(List<OrderDetails> orderDetails, List<Cart> cartList) {

        try {
            // Save all updated order details
            List<OrderDetails> saveAll = orderDetailsRepository.saveAll(orderDetails);
            // Retrieve book responses with updated quantities
            List<BookResponse> bookResponses = minusOrderDetailsBookQuantity(cartList);
            // Map the saved order details to order details response DTOs
            List<OrderDetailsResponse> orderDetailsResponseList = saveAll.stream().map(detail -> {
                return OrderDetailsResponse.builder()
                        .id(detail.getId())
                        .orderId(detail.getOrder().getId())
                        .book(bookMapper.toDto(detail.getBook()))
                        .quantity(detail.getQuantity())
                        .price(detail.getPrice())
                        .totalPrice(detail.getPrice() * detail.getQuantity())
                        .build();
            }).toList();
            // Update the book information in order details response DTOs
            orderDetailsResponseList.forEach(detail -> {
                BookResponse bookResponse = bookResponses.stream().filter(
                        book -> book.getId().equals(detail.getBook().getId())).findFirst().get();
                detail.setBook(bookResponse);
            });
            return orderDetailsResponseList;
        } catch (Exception ex) {
            // Log any database exceptions that occur
            logger.error("Error updating order details: {}", ex.getMessage());
            // Wrap and rethrow database exceptions for higher level handling
            throw new DatabaseException("updateOrderDetails: " + ex.getMessage());
        }
    }


    /**
     * Service method to update the quantity of books based on the provided list of carts.
     *
     * @param carts The list of carts
     * @return A list of updated book responses
     */
    public List<BookResponse> minusOrderDetailsBookQuantity(List<Cart> carts) {
        // Initialize logger
        Logger logger = LoggerFactory.getLogger(OrderDetailsService.class);

        try {
            // Delegate to book service to update book quantities
            return bookService.minusBookQuantity(carts);
        } catch (Exception ex) {
            // Log any database exceptions that occur
            logger.error("Error updating book quantities: {}", ex.getMessage());
            // Wrap and rethrow database exceptions for higher level handling
            throw new DatabaseException("minusOrderDetailsBookQuantity: " + ex.getMessage());
        }
    }
}
