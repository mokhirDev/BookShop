package com.mokhir.dev.BookShop.aggregation.dto.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -4915617147729757014L;
    private Long id;
    private BookResponse bookResponse;
    private Integer quantity;
    private Integer totalPrice;
}
