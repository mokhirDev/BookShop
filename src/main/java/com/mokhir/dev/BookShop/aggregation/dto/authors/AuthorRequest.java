package com.mokhir.dev.BookShop.aggregation.dto.authors;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AuthorRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8858968352859412196L;
    private String name;
    private Long id;
}
