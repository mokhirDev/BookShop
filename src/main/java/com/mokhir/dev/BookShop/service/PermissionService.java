package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionRequest;
import com.mokhir.dev.BookShop.aggregation.dto.permission.PermissionResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Permission;
import com.mokhir.dev.BookShop.aggregation.mapper.PermissionMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.repository.interfaces.PermissionRepository;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService implements EntityServiceInterface<Permission, PermissionRequest, PermissionResponse, Long> {
    private final PermissionRepository repository;
    private final PermissionMapper mapper;
    @Override
    public PermissionResponse getById(Long aLong) {
        return null;
    }

    @Override
    public Page<PermissionResponse> findAll(Pageable pageable) {
        try {
            Page<Permission> all = repository.findAll(pageable);
            if (all.isEmpty()) {
                throw new NotFoundException("Permission didn't found");
            }
            return all.map(mapper::toDto);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public PermissionResponse signUp(PermissionRequest permissionRequest) {
        return null;
    }

    @Override
    public PermissionResponse remove(PermissionRequest permissionRequest) {
        return null;
    }

    @Override
    public PermissionResponse removeById(Long aLong) {
        return null;
    }

    @Override
    public PermissionResponse update(PermissionRequest e) {
        return null;
    }

    public Permission findByName(String name) {
        try {
            Permission permissionByName = repository.findPermissionByName(name);
            if (permissionByName == null) {
                throw new NotFoundException("Permission didn't found");
            }
            return permissionByName;
        }catch (NotFoundException ex){
            throw new NotFoundException(ex.getMessage());
        }catch (Exception ex){
            throw new DatabaseException(ex.getMessage());
        }
    }
}
