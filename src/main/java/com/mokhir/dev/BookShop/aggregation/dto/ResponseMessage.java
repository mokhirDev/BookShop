package com.mokhir.dev.BookShop.aggregation.dto;

import lombok.*;
import org.springframework.data.domain.Page;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMessage<T> {
    private String message;
    private String currentUser;
    private T entities;
}
