package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import com.mokhir.dev.BookShop.aggregation.dto.users.SignInResponse;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserRequest;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Role;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import com.mokhir.dev.BookShop.aggregation.mapper.RoleMapper;
import com.mokhir.dev.BookShop.aggregation.mapper.UserMapper;
import com.mokhir.dev.BookShop.controller.SignIn;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import com.mokhir.dev.BookShop.repository.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Service
@RequiredArgsConstructor
public class UserService implements EntityServiceInterface<User, UserRequest, UserResponse, String> {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final UserMapper mapper;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RoleMapper roleMapper;


    /**
     * Retrieves a user by ID.
     *
     * @param id The ID of the user to retrieve
     * @return The UserResponse object representing the user
     * @throws NotFoundException if the user with the given ID is not found
     * @throws DatabaseException if there is an error accessing the database
     */
    public UserResponse getById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<User> byId = repository.findById(realId);
            if (byId.isEmpty()) {
                throw new NotFoundException(id + ": not found");
            }
            return mapper.toDto(byId.get());
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error retrieving user by ID: {}", id, ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Retrieves all users with pagination.
     *
     * @param pageable The pagination information
     * @return A page of UserResponse objects representing users
     * @throws NotFoundException if no users are found
     * @throws DatabaseException if there is an error accessing the database
     */
    public Page<UserResponse> findAll(Pageable pageable) {
        try {
            Page<User> all = repository.findAll(pageable);
            if (all.isEmpty()) {
                throw new NotFoundException("Book didn't found");
            }
            return all.map(mapper::toDto);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error retrieving all users", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Registers a new user.
     *
     * @param request The user request object containing user information
     * @return A UserResponse object representing the registered user
     * @throws NotFoundException  if the specified role is not found
     * @throws DatabaseException  if the user already exists or there is an error accessing the database
     */
    public UserResponse register(UserRequest request) {
        try {
            User userByUsername = repository.findUserByUsername(request.getUsername());
            if (userByUsername != null) {
                throw new DatabaseException("User already exists, with current username: " + request.getUsername());
            }

            RoleResponse byId = roleService.getById(4L);
            Role build = Role.builder()
                    .id(byId.getId())
                    .name(byId.getName())
                    .permissions(byId.getPermissions())
                    .build();
            User entity = mapper.toEntity(request);
            entity.setRole(build);
            User save = repository.save(entity);
            return mapper.toDto(save);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error registering user", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Removes a user.
     *
     * @param request The user request object containing username
     * @return A UserResponse object representing the removed user
     * @throws NotFoundException if the user doesn't exist
     * @throws DatabaseException if there is an error accessing the database
     */
    public UserResponse remove(UserRequest request) {
        try {
            User userByUsername = repository.findUserByUsername(request.getUsername());
            if (userByUsername == null) {
                throw new NotFoundException("User doesn't exist, with current username: "
                        + request.getUsername());
            }
            User user = repository.deleteUserByUsername(userByUsername.getUsername());
            return mapper.toDto(user);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error removing user", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Updates a user.
     *
     * @param request The user request object containing username
     * @return A UserResponse object representing the updated user
     * @throws NotFoundException if the user doesn't exist
     * @throws DatabaseException if there is an error accessing the database
     */
    public UserResponse update(UserRequest request) {
        try {
            User userByUsername = findUserByUsername(request.getUsername());
            if (userByUsername != null) {
                mapper.updateFromDto(request, userByUsername);
                User entity = mapper.toEntity(request);
                return mapper.toDto(entity);
            }
            throw new NotFoundException(id + ": Didn't found");
        } catch (Exception ex) {
            logger.error("Error updating user", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Adds admin role to a user.
     *
     * @param request The user request object containing username
     * @return A UserResponse object representing the updated user with admin role
     * @throws DatabaseException if there is an error accessing the database
     */
    public UserResponse addAdmin(UserRequest request) {
        try {
            User userByUsername = findUserByUsername(request.getUsername());
            RoleResponse byId = roleService.getById(2L);
            userByUsername.setRole(roleMapper.toEntityFromResponse(byId));
            repository.save(userByUsername);
            UserResponse dto = mapper.toDto(userByUsername);
            dto.setRole(byId);
            return dto;
        } catch (Exception ex) {
            logger.error("Error adding admin role to user", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Performs user sign-in.
     *
     * @param signIn The SignIn object containing username and password
     * @return A SignInResponse object representing the authentication token
     * @throws DatabaseException if there is an error accessing the database
     */
    public SignInResponse signIn(SignIn signIn) {
        try {
            User userByUsername = repository.findUserByUsername(signIn.getUsername());
            if (userByUsername == null) {
                throw new NotFoundException("User not found");
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signIn.getUsername(), signIn.getPassword()));
            SignInResponse signInResponse = jwtProvider
                    .createToken(
                            userByUsername,
                            signIn.isRememberMe());
            userByUsername.setIsActive(true);
            repository.save(userByUsername);
            return signInResponse;
        } catch (Exception ex) {
            logger.error("Error during sign-in", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }


    /**
     * Adds the role of author to a user.
     *
     * @param request The UserRequest object containing user details
     * @return A UserResponse object representing the updated user
     * @throws DatabaseException if there is an error accessing the database
     */
    public UserResponse addAuthor(UserRequest request) {
        try {
            String username = request.getUsername();
            if (!username.isBlank()) {
                User userByUsername = findUserByUsername(username);
                RoleResponse byId = roleService.getById(6L);
                userByUsername.setRole(Role.builder().id(6L).build());
                User save = repository.save(userByUsername);
                UserResponse dto = mapper.toDto(save);
                dto.setRole(byId);
                return dto;
            }
            return null;
        } catch (Exception ex) {
            logger.error("Error adding author role to user", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Finds a user by username.
     *
     * @param username The username of the user to find
     * @return The User object if found
     * @throws UsernameNotFoundException if the user is not found
     * @throws DatabaseException         if there is an error accessing the database
     */
    public User findUserByUsername(String username) {
        try {
            User userByUsername = repository.findUserByUsername(username);
            if (userByUsername != null) {
                return userByUsername;
            } else {
                throw new UsernameNotFoundException("User: %s not found".formatted(username));
            }
        } catch (UsernameNotFoundException ex) {
            logger.error("Error finding user by username", ex);
            throw new UsernameNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error accessing database", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Retrieves the current user.
     *
     * @return The UserResponse object representing the current user
     * @throws NotFoundException if the current user is not found
     * @throws DatabaseException if there is an error accessing the database
     */
    public UserResponse getCurrentUser() {
        try {
            User userByUsername = repository.findUserByUsername(jwtProvider.getCurrentUser());
            return mapper.toDto(userByUsername);
        } catch (NotFoundException ex) {
            logger.error("Current user not found", ex);
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error accessing database", ex);
            throw new DatabaseException(ex.getMessage());
        }
    }
}
