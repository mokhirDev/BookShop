package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionRequest;
import com.mokhir.dev.BookShop.aggregation.dto.role.RoleRequest;
import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Permission;
import com.mokhir.dev.BookShop.aggregation.entity.Role;
import com.mokhir.dev.BookShop.aggregation.mapper.PermissionMapper;
import com.mokhir.dev.BookShop.aggregation.mapper.RoleMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.exceptions.RoleNotFoundException;
import com.mokhir.dev.BookShop.repository.interfaces.RoleRepository;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService implements EntityServiceInterface<Role, RoleRequest, RoleResponse, Long> {
    private final RoleRepository repository;
    private final RoleMapper mapper;
    private final PermissionMapper permissionMapper;

    @Override
    public RoleResponse getById(Long id) {
        try {
            Optional<Role> byId = repository.findById(id);
            if (byId.isPresent()) {
                return mapper.toDto(byId.get());
            }
            throw new RoleNotFoundException("Role not found, with id: " + id);
        } catch (Exception ex) {
            throw new DatabaseException(ex.getCause());
        }
    }

    @Override
    public Page<RoleResponse> findAll(Pageable pageable) {
        try {
            Page<Role> all = repository.findAll(pageable);
            if (all.isEmpty()) {
                throw new NotFoundException("Role didn't found");
            }
            return all.map(mapper::toDto);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public RoleResponse register(RoleRequest roleRequest) {
        try {
            Role entity = mapper.toEntity(roleRequest);
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
    public RoleResponse remove(RoleRequest req) {
        try {
            Role byName = repository.findByName(req.getName());
            if (byName == null) {
                throw new NotFoundException("Role not found, with name: " + req.getName());
            }
            repository.delete(byName);
            return mapper.toDto(byName);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }



    @Override
    public RoleResponse update(RoleRequest req) {
        try {
            Role byName = repository.findByName(req.getName());
            if (byName == null) {
                throw new NotFoundException("Role not found, with name: " + req.getName());
            }
            mapper.updateFromDto(req, byName);
            return mapper.toDto(repository.save(byName));
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    public RoleResponse findByName(String name) {
        try {
            Role byName = repository.findByName(name);
            if (byName == null) {
                throw new NotFoundException("Role not found, with name: " + name);
            }
            return mapper.toDto(byName);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
