package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import com.mokhir.dev.BookShop.aggregation.dto.users.SignInResponse;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserRequest;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Role;
import com.mokhir.dev.BookShop.aggregation.entity.User;
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
import org.springframework.beans.factory.annotation.Value;
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
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final UserMapper mapper;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
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
            throw new DatabaseException(ex.getMessage());
        }

    }

    @Override
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
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
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
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
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
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
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
            throw new DatabaseException(ex.getMessage());
        }
    }

    public UserResponse addAdmin(UserRequest request) {
        try {
            User userByUsername = findUserByUsername(request.getUsername());
            userByUsername.setRole(Role.builder().id(2L).build());
            User save = repository.save(userByUsername);
            return mapper.toDto(save);
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

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
            throw new DatabaseException(ex.getMessage());
        }
    }


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
            throw new DatabaseException(ex.getMessage());
        }
    }

    public User findUserByUsername(String username) {
        try {
            User userByUsername = repository.findUserByUsername(username);
            if (userByUsername != null) {
                return userByUsername;
            } else {
                throw new UsernameNotFoundException("User: %s not found".formatted(username));
            }
        } catch (UsernameNotFoundException ex) {
            throw new UsernameNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

}
