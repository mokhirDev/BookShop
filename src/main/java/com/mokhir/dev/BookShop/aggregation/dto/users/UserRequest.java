package com.mokhir.dev.BookShop.aggregation.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7385136138099028561L;
    @NotNull
    @NotBlank
    @Size(min = 3)
    private String firstName;

    @NotNull
    @NotBlank
    @Size(min = 3)
    private String lastName;

    @NotNull
    @NotBlank
    @Size(min = 4, max = 30)
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 4)
    private String password;
    private RoleResponse roleResponse;
}
