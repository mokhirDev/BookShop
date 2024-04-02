package com.mokhir.dev.BookShop.aggregation.dto.authors;

import com.mokhir.dev.BookShop.aggregation.entity.Authors;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AuthorResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -6002271374669544409L;
    private Authors authors;

}
