package com.mokhir.dev.BookShop.controller;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SignIn {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private boolean rememberMe;
}
