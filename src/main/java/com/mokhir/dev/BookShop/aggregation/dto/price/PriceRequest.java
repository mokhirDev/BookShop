package com.mokhir.dev.BookShop.aggregation.dto.price;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PriceRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -140487446106809996L;
    private Long id;
    private Long book_id;
    private Long price;
}
