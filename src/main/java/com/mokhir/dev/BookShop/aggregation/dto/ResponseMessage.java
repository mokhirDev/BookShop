package com.mokhir.dev.BookShop.aggregation.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseMessage {
    private String message;
    private String currentUser;
    private List<?> entities;
}
