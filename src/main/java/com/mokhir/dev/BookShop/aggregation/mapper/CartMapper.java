package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartRequest;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.entity.OrderDetails;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartMapper implements EntityMapper<Cart, CartRequest, CartResponse> {
    private final BookMapper bookMapper;

    @Override
    @Primary
    public CartResponse toDto(Cart cart) {
        if (cart == null) {
            return null;
        }
        return CartResponse.builder()
                .id(cart.getId())
                .quantity(cart.getQuantity())
                .bookResponse(bookMapper.toDto(cart.getBook()))
                .totalPrice(cart.getQuantity() * cart.getBook().getPrice())
                .build();
    }

    @Override
    @Primary
    public Cart toEntity(CartRequest req) {
        if (req == null) {
            return null;
        }
        return Cart.builder()
                .quantity(req.getQuantity())
                .book(Book.builder().id(req.getBookId()).build())
                .build();
    }

    @Override
    @Primary
    public void updateFromDto(CartRequest req, Cart entity) {
        if (req == null) {
            return;
        }
        if (req.getBookId() != null) {
            entity.setBook(Book.builder().id(req.getBookId()).build());
        }
        if (req.getQuantity() != null) {
            entity.setQuantity(req.getQuantity());
        }
    }

    public OrderDetails toOrderDetails(Cart cart) {
        if (cart == null) {
            return null;
        }
        return OrderDetails
                .builder()
                .quantity(cart.getQuantity())
                .price(cart.getBook().getPrice())
                .book(cart.getBook())
                .build();
    }
}
