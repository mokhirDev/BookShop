package com.mokhir.dev.BookShop.aggregation.dto.price;

import com.mokhir.dev.BookShop.aggregation.entity.Prices;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PriceResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -5396479280124599587L;
    private Prices prices;
}
