package com.mokhir.dev.BookShop.aggregation.dto.users;

import com.mokhir.dev.BookShop.aggregation.entity.Users;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 8701800051752083285L;
    private Long id;
    private String login;
    private String name;
}
