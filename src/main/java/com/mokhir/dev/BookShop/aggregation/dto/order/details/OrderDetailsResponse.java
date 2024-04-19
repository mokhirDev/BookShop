package com.mokhir.dev.BookShop.aggregation.dto.order.details;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsResponse {
    private Long id;
    private BookResponse book;
    private Integer quantity;
    private Long orderId;
    private Integer price;
    private Integer totalPrice;
}
