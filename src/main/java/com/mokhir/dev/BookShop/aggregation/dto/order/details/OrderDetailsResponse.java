package com.mokhir.dev.BookShop.aggregation.dto.order.details;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsResponse {
    private Long id;
    private BookResponse book;
    private Integer quantity;
    private Integer totalPrice;
}
