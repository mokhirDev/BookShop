package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.users.UserRequest;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Users;
import com.mokhir.dev.BookShop.aggregation.mapper.UserMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import com.mokhir.dev.BookShop.repository.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements EntityServiceInterface<Users, UserRequest, UserResponse, String> {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserResponse getById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<Users> byId = repository.findById(realId);
            if (byId.isEmpty()) {
                throw new NotFoundException(id + ": not found");
            }
            Users users = byId.get();
            return mapper.toDto(users);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }

    }

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        try {
            Page<Users> all = repository.findAll(pageable);
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
    public UserResponse add(UserRequest request) {
        try {
            Users entity = mapper.toEntity(request);
            Users save = repository.save(entity);
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
            Users entity = mapper.toEntity(request);
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
            Optional<Users> byId = repository.findById(realId);
            if (byId.isPresent()) {
                repository.deleteById(realId);
                return mapper.toDto(byId.get());
            }
            throw new NotFoundException(id+": Doesn't exist");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public UserResponse update(UserRequest request) {
        try{
            Long id = request.getUsers().getId();
            Optional<Users> byId = repository.findById(id);
            if (byId.isPresent()){
                mapper.updateFromDto(request, byId.get());
                Users entity = mapper.toEntity(request);
                return mapper.toDto(entity);
            }
            throw new NotFoundException(id+": Didn't found");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }
}
