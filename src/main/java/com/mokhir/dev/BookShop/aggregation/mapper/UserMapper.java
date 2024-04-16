package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.role.RoleResponse;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserRequest;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Permission;
import com.mokhir.dev.BookShop.aggregation.entity.Role;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.EntityMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class UserMapper implements EntityMapper<User, UserRequest, UserResponse> {

    @Override
    @Primary
    public UserResponse toDto(User entity) {
        if (entity == null) {
            return null;
        }
        return UserResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .role(roleToRoleResponse(entity.getRole()))
                .userName(entity.getUsername())
                .build();
    }

    @Override
    @Primary
    public User toEntity(UserRequest req) {
        if (req == null) {
            return null;
        }

        return User
                .builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .password(new BCryptPasswordEncoder().encode(req.getPassword()))
                .username(req.getUsername())
                //Role yaratilmaydi, sababi User entitisini yaratganidan kiyin
                //o'zi hohlagan roleni yaratib ketish uchun!
                //.role()
                .build();
    }

    @Override
    @Primary
    public void updateFromDto(UserRequest req, User entity) {
        if (req == null) {
            return;
        }
        if (req.getFirstName() != null) {
            entity.setFirstName(req.getFirstName());
        }
        if (req.getLastName() != null) {
            entity.setLastName(req.getLastName());
        }
        if (req.getUsername() != null) {
            entity.setUsername(req.getUsername());
        }
        if (req.getPassword() != null) {
            entity.setPassword(new BCryptPasswordEncoder().encode(req.getPassword()));
        }
    }

    protected RoleResponse roleToRoleResponse(Role role) {
        if (role == null) {
            return null;
        }

        RoleResponse.RoleResponseBuilder roleResponse = RoleResponse.builder();
        roleResponse.id(role.getId());
        roleResponse.name(role.getName());
        Set<Permission> set = role.getPermissions();
        if (set != null) {
            roleResponse.permissions(new LinkedHashSet<>(set));
        }
        return roleResponse.build();
    }
}
