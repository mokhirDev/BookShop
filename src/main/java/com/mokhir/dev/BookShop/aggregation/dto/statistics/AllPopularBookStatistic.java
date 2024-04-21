package com.mokhir.dev.BookShop.aggregation.dto.statistics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AllPopularBookStatistic {
    private String book_name;
    private Long orders_count;
    private Long comments_count;
}
