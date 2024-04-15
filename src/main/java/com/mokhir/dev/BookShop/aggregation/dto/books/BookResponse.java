package com.mokhir.dev.BookShop.aggregation.dto.books;

import com.mokhir.dev.BookShop.aggregation.entity.Books;
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
    private static final long serialVersionUID = -4529422928749934475L;
    private Books book;
}
