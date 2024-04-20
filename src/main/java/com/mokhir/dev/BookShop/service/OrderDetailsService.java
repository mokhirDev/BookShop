package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.dto.order.details.OrderDetailsResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.entity.OrderDetails;
import com.mokhir.dev.BookShop.aggregation.mapper.BookMapper;
import com.mokhir.dev.BookShop.aggregation.mapper.CartMapper;
import com.mokhir.dev.BookShop.aggregation.mapper.OrderDetailsMapper;
import com.mokhir.dev.BookShop.aggregation.mapper.OrderMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.OrderDetailsRepository;
import lombok.RequiredArgsConstructor;
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
    private final OrderMapper orderMapper;
    private final OrderDetailsMapper orderDetailsMapper;

    public List<OrderDetails> create(List<Cart> carts) {
        try {
            List<OrderDetails> detailsList = carts.stream().map(cartMapper::toOrderDetails).toList();
            return orderDetailsRepository.saveAll(detailsList);
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    public Page<OrderDetailsResponse> getAll(int page, int size) {
        try {
            List<OrderDetails> allByCreatedBy = orderDetailsRepository.findAllByCreatedBy(jwtProvider.getCurrentUser());
            List<OrderDetailsResponse> list = allByCreatedBy.stream().map(orderDetailsMapper::toDto).toList();
            return new PageImpl<>(list, PageRequest.of(page, size), list.size());
        }catch (Exception e){
            throw new DatabaseException("OrderDetailsService: getAll: "+e.getMessage());
        }
    }

    public List<OrderDetailsResponse> updateOrderDetails(List<OrderDetails> orderDetails, List<Cart> cartList) {
        try {
            List<OrderDetails> saveAll = orderDetailsRepository.saveAll(orderDetails);
            List<BookResponse> bookResponses = minusOrderDetailsBookQuantity(cartList);
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
            orderDetailsResponseList.forEach(detail -> {
                BookResponse bookResponse = bookResponses.stream().filter(
                        book -> book.getId()
                                .equals(detail.getBook().getId())).peek(System.out::println).findFirst().get();
                detail.setBook(bookResponse);
            });
            return orderDetailsResponseList;
        } catch (Exception ex) {
            throw new DatabaseException("updateOrderDetails: " + ex.getMessage());
        }
    }

    public List<BookResponse> minusOrderDetailsBookQuantity(List<Cart> carts) {
        try {
            return bookService.minusBookQuantity(carts);
        } catch (Exception ex) {
            throw new DatabaseException("minusOrderDetailsBookQuantity: " + ex.getMessage());
        }
    }
}
