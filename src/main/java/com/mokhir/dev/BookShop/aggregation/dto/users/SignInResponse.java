package com.mokhir.dev.BookShop.aggregation.dto.users;

import com.mokhir.dev.BookShop.aggregation.entity.Role;
import lombok.*;
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Role role;
    private String token;
    private String refreshToken;
}
