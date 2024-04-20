package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionRequest;
import com.mokhir.dev.BookShop.aggregation.dto.role.RoleRequest;
import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Permission;
import com.mokhir.dev.BookShop.aggregation.entity.Role;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.EntityMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class RoleMapper implements EntityMapper<Role, RoleRequest, RoleResponse> {

    @Override
    @Primary
    public RoleResponse toDto(Role entity) {
        if (entity == null) {
            return null;
        }
        RoleResponse roleResponse = RoleResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .permissions(entity.getPermissions())
                .build();

        Set<Permission> set = entity.getPermissions();
        if (set != null) {
            roleResponse.setPermissions(set);
        }
        return roleResponse;
    }

    @Override
    @Primary
    public Role toEntity(RoleRequest req) {
        if (req == null) {
            return null;
        }

        return Role.builder()
                .name(req.getName())
                .permissions(permissionRequestSetToPermissionSet(req.getPermissions()))
                .build();
    }

    public Role toEntityFromResponse(RoleResponse response) {
        if (response == null) {
            return null;
        }

        return Role.builder()
                .id(response.getId())
                .name(response.getName())
                .permissions(response.getPermissions())
                .build();
    }

    @Override
    @Primary
    public void updateFromDto(RoleRequest req, Role entity) {
        if (req == null) {
            return;
        }
        if (req.getName() != null) {
            entity.setName(req.getName());
        }
        if (entity.getPermissions() != null) {
            Set<Permission> set = permissionRequestSetToPermissionSet(req.getPermissions());
            if (set != null) {
                entity.getPermissions().clear();
                entity.getPermissions().addAll(set);
            }
        } else {
            Set<Permission> set = permissionRequestSetToPermissionSet(req.getPermissions());
            if (set != null) {
                entity.setPermissions(set);
            }
        }
    }

    protected Set<Permission> permissionRequestSetToPermissionSet(Set<PermissionRequest> set) {
        if (set == null) {
            return null;
        }
        Set<Permission> set1 = new LinkedHashSet<Permission>(Math.max((int) (set.size() / .75f) + 1, 16));
        for (PermissionRequest permissionRequest : set) {
            set1.add(permissionRequestToPermission(permissionRequest));
        }
        return set1;
    }

    protected Permission permissionRequestToPermission(PermissionRequest permissionRequest) {
        if (permissionRequest == null) {
            return null;
        }
        Permission.PermissionBuilder permission = Permission.builder();
        permission.id(permissionRequest.getId());
        return permission.build();
    }
}