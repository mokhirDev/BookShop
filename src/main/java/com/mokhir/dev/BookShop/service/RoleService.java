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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);


    /**
     * Retrieves a role by its identifier.
     *
     * @param id The identifier of the role
     * @return A RoleResponse object containing information about the role
     * @throws RoleNotFoundException if the role with the specified identifier is not found
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public RoleResponse getById(Long id) {
        try {
            Optional<Role> byId = repository.findById(id);
            if (byId.isPresent()) {
                return mapper.toDto(byId.get());
            }
            throw new RoleNotFoundException("Role not found, with id: " + id);
        } catch (Exception ex) {
            logger.error("Error retrieving role by id: {}", id, ex);
            throw new DatabaseException(ex.getCause());
        }
    }



    /**
     * Retrieves all roles with pagination.
     *
     * @param pageable The Pageable object specifying the pagination parameters
     * @return A Page object containing a list of RoleResponse objects
     * @throws NotFoundException if no roles are found
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public Page<RoleResponse> findAll(Pageable pageable) {
        try {
            Page<Role> all = repository.findAll(pageable);
            if (all.isEmpty()) {
                throw new NotFoundException("Role not found");
            }
            return all.map(mapper::toDto);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error retrieving all roles", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Registers a new role.
     *
     * @param roleRequest The RoleRequest object containing the role details
     * @return The created RoleResponse object
     * @throws DatabaseException if there is an error accessing the database
     */
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
            logger.error("Error registering role", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }


    /**
     * Removes a role by name.
     *
     * @param req The RoleRequest object containing the role name to be removed
     * @return The removed RoleResponse object
     * @throws NotFoundException if the role is not found
     * @throws DatabaseException if there is an error accessing the database
     */
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
            logger.error("Error removing role", ex);
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error removing role", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }



    /**
     * Updates a role.
     *
     * @param req The RoleRequest object containing the role details to be updated
     * @return The updated RoleResponse object
     * @throws NotFoundException if the role is not found
     * @throws DatabaseException if there is an error accessing the database
     */
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
            logger.error("Error updating role", ex);
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error updating role", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Finds a role by its name.
     *
     * @param name The name of the role to find
     * @return The RoleResponse object corresponding to the found role
     * @throws NotFoundException if the role is not found
     * @throws DatabaseException if there is an error accessing the database
     */
    public RoleResponse findByName(String name) {
        try {
            Role byName = repository.findByName(name);
            if (byName == null) {
                throw new NotFoundException("Role not found, with name: " + name);
            }
            return mapper.toDto(byName);
        } catch (NotFoundException e) {
            logger.error("Error finding role by name", e);
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error finding role by name", e);
            throw new DatabaseException(e.getMessage());
        }
    }
}
