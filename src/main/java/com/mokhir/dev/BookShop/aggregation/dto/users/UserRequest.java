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
public class UserRequest implements Serializable{
    @Serial
    private static final long serialVersionUID = 8369598430108811520L;
    private Users users;
}
