package com.mokhir.dev.BookShop.aggregation.dto.books;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -140487446106809996L;
    private Long id;
    private Long author_id;
    private String name;
}
