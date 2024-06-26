package com.mokhir.dev.BookShop.service.interfaces;

import com.mokhir.dev.BookShop.aggregation.dto.ResponseMessage;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartRequest;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartServiceInterface {
    CartResponse addToCart(CartRequest cartRequest);
    CartResponse removeFromCart(CartRequest cartRequest);
    CartResponse getCart(CartRequest cartRequest);
    ResponseMessage<Page<CartResponse>> getAllCarts(Pageable pageable);
    ResponseMessage<CartResponse> deleteCart(CartRequest cartRequest);
    ResponseMessage<List<CartResponse>> deleteAllCarts();
}
