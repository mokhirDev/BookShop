package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Books;
import com.mokhir.dev.BookShop.aggregation.mapper.BookMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import com.mokhir.dev.BookShop.repository.interfaces.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BookService
        implements EntityServiceInterface<Books, BookRequest, BookResponse, String> {
    private final BookRepository repository;
    private final BookMapper mapper;
    @Override
    public BookResponse getById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<Books> byId = repository.findById(realId);
            if (byId.isEmpty()) {
                throw new NotFoundException(realId + ": not found");
            }
            Books books = byId.get();
            return mapper.toDto(books);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }

    }

    @Override
    public Page<BookResponse> findAll(Pageable pageable) {
        try {
            Page<Books> all = repository.findAll(pageable);
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
    public BookResponse add(BookRequest request) {
        try {
            Books entity = mapper.toEntity(request);
            Books save = repository.save(entity);
            return mapper.toDto(save);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public BookResponse remove(BookRequest request) {
        try {
            Books entity = mapper.toEntity(request);
            repository.delete(entity);
            return mapper.toDto(entity);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public BookResponse removeById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<Books> byId = repository.findById(realId);
            if (byId.isPresent()) {
                repository.deleteById(realId);
                return mapper.toDto(byId.get());
            }
            throw new NotFoundException(realId+": Doesn't exist");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public BookResponse update(BookRequest request) {
        try{
            Long id = request.getId();
            Optional<Books> byId = repository.findById(id);
            if (byId.isPresent()){
                mapper.updateFromDto(request, byId.get());
                Books entity = mapper.toEntity(request);
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
