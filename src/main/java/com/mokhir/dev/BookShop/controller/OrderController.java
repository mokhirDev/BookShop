package com.mokhir.dev.BookShop.controller;

import com.mokhir.dev.BookShop.aggregation.dto.order.OrderRequest;
import com.mokhir.dev.BookShop.aggregation.dto.order.OrderResponse;
import com.mokhir.dev.BookShop.aggregation.dto.order.details.OrderDetailsResponse;
import com.mokhir.dev.BookShop.service.OrderDetailsService;
import com.mokhir.dev.BookShop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderDetailsService orderDetailsService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<Page<OrderResponse>> getAll(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok().body(orderService.getAll(page, size));
    }

    @GetMapping("/detail/all")
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<Page<OrderDetailsResponse>> getAllDetail(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok().body(orderDetailsService.getAll(page, size));
    }


    @GetMapping
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<OrderResponse> getLast() {
        return ResponseEntity.ok().body(orderService.getLast());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER_ACCESS')")
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request) {
        return ResponseEntity.ok().body(orderService.createOrder(request));
    }


}
