package com.mokhir.dev.BookShop.aggregation.dto.order;

import com.mokhir.dev.BookShop.aggregation.dto.cart.CartRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private List<Long> cartIds;
}
