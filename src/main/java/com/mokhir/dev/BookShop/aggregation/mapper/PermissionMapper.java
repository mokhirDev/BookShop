package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionRequest;
import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Permission;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.EntityMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper implements EntityMapper<Permission, PermissionRequest, PermissionResponse> {
    @Override
    @Primary
    public PermissionResponse toDto(Permission entity) {
        if (entity == null) {
            return null;
        }
        return PermissionResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Override
    @Primary
    public Permission toEntity(PermissionRequest req) {
        if (req == null) {
            return null;
        }
        return Permission.builder()
                .id(req.getId())
                .build();
    }

    @Override
    @Primary
    public void updateFromDto(PermissionRequest req, Permission entity) {
        if (req == null) {
            return;
        }
        if (req.getId() != null) {
            entity.setId(req.getId());
        }
    }
}
