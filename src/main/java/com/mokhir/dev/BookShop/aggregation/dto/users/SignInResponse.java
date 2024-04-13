package com.mokhir.dev.BookShop.aggregation.dto.users;

import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInResponse {
    private Long id;

    private String username;

    private String firstName;

    private String lastName;

    private RoleResponse role;

    private String token;

    private String refreshToken;
}
