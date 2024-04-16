package com.mokhir.dev.BookShop.aggregation.dto.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mokhir.dev.BookShop.aggregation.entity.DateAudit;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CartRequest extends DateAudit implements Serializable {
    @Serial
    private static final long serialVersionUID = 8757150889322423303L;
    @JsonProperty("cartId")
    private Long cartId;
    @JsonProperty("book_id")
    private Long bookId;
    @JsonProperty("quantity")
    private Integer quantity;
}
