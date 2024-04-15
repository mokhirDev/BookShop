package com.mokhir.dev.BookShop.aggregation.dto.comments;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2678671092945804184L;
    private Long id;
    @JsonProperty("text")
    @Size(min = 5, max = 50)
    private String text;
    @JsonProperty("book_id")
    private Long bookId;
}
