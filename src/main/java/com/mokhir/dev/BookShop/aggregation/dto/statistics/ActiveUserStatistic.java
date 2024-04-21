package com.mokhir.dev.BookShop.aggregation.dto.statistics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ActiveUserStatistic {
    private Long id;
    private String name;
    private Long bookCount;
    private Long totalAmount;
    private Long commentCount;
}
