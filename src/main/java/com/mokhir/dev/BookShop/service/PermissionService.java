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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PermissionService implements EntityServiceInterface<Permission, PermissionRequest, PermissionResponse, Long> {
    private final PermissionRepository repository;
    private final PermissionMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);


    /**
     * Retrieves a permission by its identifier.
     *
     * @param aLong The identifier of the permission
     * @return The permission response
     * @throws NotFoundException if the permission with the specified identifier is not found
     */
    @Override
    public PermissionResponse getById(Long aLong) {
        try {
            // Retrieve permission by its identifier
            Optional<Permission> byId = repository.findById(aLong);
            if (byId.isEmpty()) {
                throw new NotFoundException("Permission not found");
            }
            Permission permission = byId.get();
            return mapper.toDto(permission);
        } catch (NotFoundException e) {
            // Log not found exceptions and rethrow them
            logger.error("Permission not found: {}", e.getMessage());
            throw new NotFoundException(e.getMessage());
        }
    }

    /**
     * Retrieves all permissions with pagination.
     *
     * @param pageable The Pageable object specifying the pagination parameters
     * @return A Page object containing a list of PermissionResponse objects
     * @throws NotFoundException if no permissions are found
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public Page<PermissionResponse> findAll(Pageable pageable) {
        try {
            // Retrieve all permissions with pagination
            Page<Permission> all = repository.findAll(pageable);
            if (all.isEmpty()) {
                throw new NotFoundException("Permission not found");
            }
            return all.map(mapper::toDto);
        } catch (NotFoundException ex) {
            // Log not found exceptions and rethrow them
            logger.error("Permission not found: {}", ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Log other exceptions and rethrow them as DatabaseException
            logger.error("Database exception: {}", ex.getMessage());
            throw new DatabaseException(ex.getMessage());
        }
    }


    /**
     * Registers a new permission based on the provided PermissionRequest object.
     *
     * @param permissionRequest The PermissionRequest object containing
     * information about the permission to be registered
     * @return A PermissionResponse object containing information about the registered permission
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public PermissionResponse register(PermissionRequest permissionRequest) {
        try {
            // Map the PermissionRequest object to a Permission entity and save it
            Permission entity = mapper.toEntity(permissionRequest);
            repository.save(entity);
            // Return the mapped Permission entity as a PermissionResponse object
            return mapper.toDto(entity);
        } catch (Exception ex) {
            // Log the database exception and rethrow it as a DatabaseException
            logger.error("Database exception: {}", ex.getMessage());
            throw new DatabaseException(ex.getCause());
        }
    }

    /**
     * Removes a permission based on the provided PermissionRequest object.
     *
     * @param req The PermissionRequest object containing the ID of the permission to be removed
     * @return A PermissionResponse object containing information about the removed permission
     * @throws NotFoundException if the permission with the specified ID is not found
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public PermissionResponse remove(PermissionRequest req) {
        try {
            // Find the permission in the database by its ID
            Permission permissionById = repository.findPermissionById(req.getId());
            if (permissionById == null) {
                throw new NotFoundException("Permission not found with id: " + req.getId());
            }
            // Delete the permission from the repository
            repository.delete(permissionById);
            // Return the mapped Permission entity as a PermissionResponse object
            return mapper.toDto(permissionById);
        } catch (NotFoundException ex) {
            // Log and rethrow NotFoundException
            logger.error("Permission not found: {}", ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Log the database exception and rethrow it as a DatabaseException
            logger.error("Database exception: {}", ex.getMessage());
            throw new DatabaseException(ex.getMessage());
        }
    }


    /**
     * Updates a permission based on the provided PermissionRequest object.
     *
     * @param req The PermissionRequest object containing the ID and updated information of the permission
     * @return A PermissionResponse object containing information about the updated permission
     * @throws NotFoundException if the permission with the specified ID is not found
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public PermissionResponse update(PermissionRequest req) {
        try {
            // Find the permission in the database by its ID
            Permission byId = repository.findPermissionById(req.getId());
            if (byId == null) {
                throw new NotFoundException("Permission not found with id: " + req.getId());
            }
            // Update the permission entity with the information from the request
            mapper.updateFromDto(req, byId);
            // Save the updated permission to the repository and return the mapped result
            return mapper.toDto(repository.save(byId));
        } catch (NotFoundException ex) {
            // Log and rethrow NotFoundException
            logger.error("Permission not found: {}", ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Log the database exception and rethrow it as a DatabaseException
            logger.error("Database exception: {}", ex.getMessage());
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Finds a permission by its name.
     *
     * @param name The name of the permission to find
     * @return The Permission object with the specified name
     * @throws NotFoundException if the permission with the specified name is not found
     * @throws DatabaseException if there is an error accessing the database
     */
    public Permission findByName(String name) {
        try {
            // Find the permission in the database by its name
            Permission permissionByName = repository.findPermissionByName(name);
            if (permissionByName == null) {
                throw new NotFoundException("Permission not found with name: " + name);
            }
            // Return the found permission
            return permissionByName;
        } catch (NotFoundException ex) {
            // Log and rethrow NotFoundException
            logger.error("Permission not found: {}", ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Log the database exception and rethrow it as a DatabaseException
            logger.error("Database exception: {}", ex.getMessage());
            throw new DatabaseException(ex.getMessage());
        }
    }
}
