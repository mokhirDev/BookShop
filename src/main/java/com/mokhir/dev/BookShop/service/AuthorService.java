package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.authors.AuthorRequest;
import com.mokhir.dev.BookShop.aggregation.dto.authors.AuthorResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Authors;
import com.mokhir.dev.BookShop.aggregation.mapper.AuthorMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.repository.interfaces.AuthorRepository;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthorService
        implements EntityServiceInterface<Authors, AuthorRequest, AuthorResponse, String> {

    private final AuthorRepository repository;
    private final AuthorMapper mapper;

    @Override
    public AuthorResponse getById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<Authors> byId = repository.findById(realId);
            if (byId.isEmpty()) {
                throw new NotFoundException(id + ": not found");
            }
            Authors authors = byId.get();
            return mapper.toDto(authors);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }

    }

    @Override
    public Page<AuthorResponse> findAll(Pageable pageable) {
        try {
            Page<Authors> all = repository.findAll(pageable);
            if (all.isEmpty()) {
                throw new NotFoundException("Author didn't found");
            }
            return all.map(mapper::toDto);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public AuthorResponse add(AuthorRequest request) {
        try {
            Authors entity = mapper.toEntity(request);
            Authors save = repository.save(entity);
            return mapper.toDto(save);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public AuthorResponse remove(AuthorRequest request) {
        try {
            Authors entity = mapper.toEntity(request);
            repository.delete(entity);
            return mapper.toDto(entity);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public AuthorResponse removeById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<Authors> byId = repository.findById(realId);
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
    public AuthorResponse update(AuthorRequest request) {
       try{
           Long id = request.getId();
           Optional<Authors> byId = repository.findById(id);
           if (byId.isPresent()){
               mapper.updateFromDto(request, byId.get());
               Authors entity = mapper.toEntity(request);
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
