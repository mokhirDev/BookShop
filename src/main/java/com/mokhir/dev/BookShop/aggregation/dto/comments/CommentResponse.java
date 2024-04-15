package com.mokhir.dev.BookShop.aggregation.dto.comments;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CommentResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -6053162219178407025L;
    private Long id;
    private String text;
    private BookResponse book;
    private String createdAt;
    private String createdBy;
}
