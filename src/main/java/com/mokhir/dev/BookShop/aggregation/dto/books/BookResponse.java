package com.mokhir.dev.BookShop.aggregation.dto.books;

import com.mokhir.dev.BookShop.aggregation.entity.Book;
import jakarta.persistence.Column;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 2348263146096464200L;
    private Long id;
    private String name;
    private Integer price;
    private Integer quantity;
}
