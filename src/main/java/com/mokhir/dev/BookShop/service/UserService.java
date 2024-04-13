package com.mokhir.dev.BookShop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import com.mokhir.dev.BookShop.aggregation.dto.users.SignInResponse;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserRequest;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Permission;
import com.mokhir.dev.BookShop.aggregation.entity.Role;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import com.mokhir.dev.BookShop.aggregation.mapper.UserMapper;
import com.mokhir.dev.BookShop.controller.SignIn;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.RoleRepository;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import com.mokhir.dev.BookShop.repository.interfaces.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;


@Service
@RequiredArgsConstructor
public class UserService implements EntityServiceInterface<User, UserRequest, UserResponse, String> {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository repository;
    private final UserMapper mapper;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;
    @Value("${jwt.token.secret}")
    private String secret;

    @Override
    public UserResponse getById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<User> byId = repository.findById(realId);
            if (byId.isEmpty()) {
                throw new NotFoundException(id + ": not found");
            }
            User user = byId.get();
            return mapper.toDto(user);
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
    public UserResponse signUp(UserRequest request) {
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
            entity.setUsername(request.getUsername());
            entity.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
            entity.setFirstName(request.getFirstName());
            entity.setLastName(request.getLastName());
            entity.setRole(build);
            User save = repository.save(entity);
            UserResponse dto = mapper.toDto(save);
            dto.setFirstName(save.getFirstName());
            dto.setLastName(save.getLastName());
            dto.setUserName(save.getUsername());
            dto.setRole(save.getRole());
            return dto;
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public UserResponse remove(UserRequest request) {
        try {
            User entity = mapper.toEntity(request);
            repository.delete(entity);
            return mapper.toDto(entity);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public UserResponse removeById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<User> byId = repository.findById(realId);
            if (byId.isPresent()) {
                repository.deleteById(realId);
                return mapper.toDto(byId.get());
            }
            throw new NotFoundException(id + ": Doesn't exist");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public UserResponse update(UserRequest request) {
        try {
            String username = request.getUsername();
            User userByUsername = repository.findUserByUsername(username);
            if (userByUsername != null) {
                mapper.updateFromDto(request, userByUsername);
                User entity = mapper.toEntity(request);
                return mapper.toDto(entity);
            }
            throw new NotFoundException(id + ": Didn't found");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    public UserResponse addAdmin(UserRequest request) {
        try {
            String username = request.getUsername();
            User userByUsername = repository.findUserByUsername(username);
            if (userByUsername == null) {
                throw new UsernameNotFoundException("User with id:%s didn't found".formatted(username));
            }
            userByUsername.setRole(Role.builder().id(2L).build());
            User save = repository.save(userByUsername);
            return mapper.toDto(save);
        } catch (UsernameNotFoundException ex) {
            throw new UsernameNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    public SignInResponse signIn(SignIn signIn) {
        try {
            User userByUsername = repository.findUserByUsername(signIn.getUsername());
            if (userByUsername == null ||
                    !(new BCryptPasswordEncoder().matches(signIn.getPassword(), userByUsername.getPassword()))) {
                throw new UsernameNotFoundException("User: %s not found".formatted(signIn.getUsername()));
            }
            SignInResponse signInResponse = jwtProvider
                    .createToken(
                            userByUsername,
                            signIn.isRememberMe());

            Authentication authentication = jwtProvider.getAuthentication(signInResponse.getToken());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            userByUsername.setIsActive(true);
            repository.save(userByUsername);
            return signInResponse;
        } catch (UsernameNotFoundException ex) {
            throw new UsernameNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }


}
