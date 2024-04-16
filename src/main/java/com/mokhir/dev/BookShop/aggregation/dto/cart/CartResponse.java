package com.mokhir.dev.BookShop.aggregation.dto.cart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 8757150889322423303L;
    private Long id;
    @JsonProperty("bookId")
    private Long bookId;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("price")
    private Integer price;
    @JsonProperty("totalPrice")
    private Integer totalPrice;
}
