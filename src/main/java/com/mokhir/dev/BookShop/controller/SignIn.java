package com.mokhir.dev.BookShop.controller;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SignIn implements Serializable {
    @Serial
    private static final long serialVersionUID = 2228276562424152799L;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private boolean rememberMe;
}
