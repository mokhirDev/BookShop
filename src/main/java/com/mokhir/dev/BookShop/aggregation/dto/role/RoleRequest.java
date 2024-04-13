package com.mokhir.dev.BookShop.aggregation.dto.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionRequest;
import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoleRequest {
    @NotNull
    @NotBlank
    @JsonProperty(namespace = "name")
    private String name;
    @NotNull
    @NotBlank
    @JsonProperty(namespace = "permissions")
    private Set<PermissionRequest> permissions;
}
