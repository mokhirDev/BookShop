package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.ResponseMessage;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartRequest;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartResponse;
import com.mokhir.dev.BookShop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<CartResponse> addCart(@RequestBody CartRequest cartRequest) {
        CartResponse cartResponse = cartService.addToCart(cartRequest);
        return ResponseEntity.ok().body(cartResponse);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<ResponseMessage<Page<CartResponse>>> getAllCarts(@RequestParam("page") int pageIndex,
                                                                           @RequestParam("size") int pageSize) {
        PageRequest pageable = PageRequest.of(pageIndex, pageSize);
        return ResponseEntity.ok().body(cartService.getAllCarts(pageable));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<ResponseMessage<CartResponse>> deleteCart(@RequestBody CartRequest cartRequest) {
        return ResponseEntity.ok().body(cartService.deleteCart(cartRequest));
    }

    @DeleteMapping("/delete/all")
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<ResponseMessage<List<CartResponse>>> deleteAllCarts() {
        return ResponseEntity.ok().body(cartService.deleteAllCarts());
    }
}