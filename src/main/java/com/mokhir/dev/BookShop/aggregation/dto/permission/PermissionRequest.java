package com.mokhir.dev.BookShop.aggregation.dto.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRequest implements Serializable {
    @NotNull
    @NotBlank
    private Long id;
}
