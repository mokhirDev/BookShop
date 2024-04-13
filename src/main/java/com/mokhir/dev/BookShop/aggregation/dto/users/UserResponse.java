package com.mokhir.dev.BookShop.aggregation.dto.users;

import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Role;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 8701800051752083285L;
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private RoleResponse role;
}
