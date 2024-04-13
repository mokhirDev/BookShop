package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionRequest;
import com.mokhir.dev.BookShop.aggregation.dto.role.RoleRequest;
import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Permission;
import com.mokhir.dev.BookShop.aggregation.entity.Role;
import com.mokhir.dev.BookShop.aggregation.mapper.PermissionMapper;
import com.mokhir.dev.BookShop.aggregation.mapper.RoleMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.RoleNotFoundException;
import com.mokhir.dev.BookShop.repository.interfaces.PermissionRepository;
import com.mokhir.dev.BookShop.repository.interfaces.RoleRepository;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService implements EntityServiceInterface<Role, RoleRequest, RoleResponse, Long> {
    private final RoleRepository repository;
    private final RoleMapper mapper;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public RoleResponse getById(Long id) {
        try {
            Optional<Role> byId = repository.findById(id);
            if (byId.isPresent()) {
                Role role = byId.get();
                RoleResponse dto = mapper.toDto(role);
                dto.setName(role.getName());
                dto.setId(role.getId());
                dto.setPermissions(role.getPermissions());
                return dto;
            }
            throw new RoleNotFoundException("Role not found, with id: " + id);
        } catch (Exception ex) {
            throw new DatabaseException(ex.getCause());
        }
    }

    @Override
    public Page<RoleResponse> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public RoleResponse signUp(RoleRequest roleRequest) {
        try {
            Role entity = mapper.toEntity(roleRequest);
            entity.setName(roleRequest.getName());
            Set<PermissionRequest> permissions = roleRequest.getPermissions();
            Set<Permission> permissionsEntity = permissions
                    .stream()
                    .map(permissionMapper::toEntity)
                    .collect(Collectors.toSet());
            entity.setPermissions(permissionsEntity);
            return mapper.toDto(repository.save(entity));
        } catch (Exception ex) {
            throw new DatabaseException(ex.getCause());
        }
    }

    @Override
    public RoleResponse remove(RoleRequest roleRequest) {
        return null;
    }

    @Override
    public RoleResponse removeById(Long aLong) {
        return null;
    }

    @Override
    public RoleResponse update(RoleRequest e) {
        return null;
    }

//    public RoleResponse update(Role e) {
//        return repository;
//    }

    public RoleResponse findByName(String name) {
        Role byName = repository.findByName(name);
        RoleResponse dto = mapper.toDto(byName);
        dto.setName(byName.getName());
        dto.setId(byName.getId());
        dto.setPermissions(
                byName.getPermissions());
        return dto;
    }
}
