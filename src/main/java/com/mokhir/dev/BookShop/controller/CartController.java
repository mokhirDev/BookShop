package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.cart.CartRequest;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartResponse;
import com.mokhir.dev.BookShop.service.CartService;
import io.github.jhipster.web.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<CartResponse> addCart(@RequestBody CartRequest cartRequest) {
        System.out.println("========> ishladi");
        CartResponse cartResponse = cartService.addToCart(cartRequest);
        return ResponseEntity.ok().body(cartResponse);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<Page<CartResponse>> getAllCarts(@RequestParam("page") int pageIndex,
                                                          @RequestParam("size") int pageSize,
                                                          @RequestParam MultiValueMap<String, String> queryParams,
                                                          UriComponentsBuilder uriBuilder) {
        PageRequest pageable = PageRequest.of(pageIndex, pageSize);
        Page<CartResponse> page = cartService.getAllCarts(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page);
    }
}