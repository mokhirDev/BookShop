package com.mokhir.dev.BookShop.aggregation.dto.statistics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PopularBookResponse {
    private Long bookId;
    private String author;
    private String bookName;
    private Long totalQuantity;
}
