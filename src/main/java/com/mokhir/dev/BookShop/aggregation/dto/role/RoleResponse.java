package com.mokhir.dev.BookShop.aggregation.dto.role;

import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Permission;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoleResponse {
    private Long id;
    private String name;
    private Set<Permission> permissions;
}
