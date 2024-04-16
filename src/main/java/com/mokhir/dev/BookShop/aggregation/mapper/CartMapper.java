package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartRequest;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.EntityMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class CartMapper implements EntityMapper<Cart, CartRequest, CartResponse> {

    @Override
    @Primary
    public CartResponse toDto(Cart cart) {
        if (cart == null) {
            return null;
        }
        return CartResponse.builder()
                .bookId(cart.getBookId().getId())
                .totalPrice(cart.getQuantity() * cart.getBookId().getPrice())
                .price(cart.getBookId().getPrice())
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
                .bookId(Book.builder().id(req.getBookId()).build())
                .build();
    }

    @Override
    @Primary
    public void updateFromDto(CartRequest req, Cart entity) {
        if (req == null) {
            return;
        }
        if (req.getBookId() != null) {
            entity.setBookId(Book.builder().id(req.getBookId()).build());
        }
        if (req.getQuantity() != null) {
            entity.setQuantity(req.getQuantity());
        }
    }
}
