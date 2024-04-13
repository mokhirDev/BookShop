package com.mokhir.dev.BookShop.aggregation.dto.books;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mokhir.dev.BookShop.aggregation.entity.DateAudit;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BookRequest extends DateAudit implements Serializable {
    @Serial
    private static final long serialVersionUID = -140487446106809996L;
    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("price")
    private Integer price;

    @NotNull
    @JsonProperty("quantity")
    private Integer quantity;
}
