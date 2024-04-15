package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Books;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import com.mokhir.dev.BookShop.aggregation.mapper.BookMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import com.mokhir.dev.BookShop.repository.interfaces.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BookService
        implements EntityServiceInterface<Books, BookRequest, BookResponse, String> {
    private final BookRepository repository;
    private final BookMapper mapper;
    private final UserService userService;

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

    public Page<BookResponse> findAllBooksByCreatedBy(BookRequest request, Pageable pageable) {
        try {
            String createdBy = request.getCreatedBy();
            Page<Books> allBooksByCreatedBy = repository.findAllBooksByCreatedBy(createdBy, pageable);
            return allBooksByCreatedBy.map(mapper::toDto);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public BookResponse signUp(BookRequest request) {
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
            throw new NotFoundException(realId + ": Doesn't exist");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public BookResponse update(BookRequest request) {
        try {
            String createdBy = request.getCreatedBy();
            Optional<Books> byId = repository.findBooksByCreatedBy(createdBy);
            if (byId.isPresent()) {
                mapper.updateFromDto(request, byId.get());
                Books entity = mapper.toEntity(request);
                return mapper.toDto(entity);
            }
            throw new NotFoundException(createdBy + ": Didn't found");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    public BookResponse addBook(BookRequest request) {
        try {
            String createdBy = request.getCreatedBy();
            if (!createdBy.isBlank()) {
                List<Books> booksByCreatedBy = repository.findAllBooksByCreatedBy(createdBy);
                for (Books book : booksByCreatedBy) {
                    String bookName = book.getName().replaceAll(" ", "").toLowerCase();
                    String newBookName = request.getName().replaceAll(" ", "").toLowerCase();
                    if (bookName.equals(newBookName)) {
                        throw new DatabaseException(bookName + ": Book already exists");
                    }
                }
                User userByUsername = userService.findUserByUsername(createdBy);
                if (userByUsername != null) {
                    Books build = Books.builder()
                            .quantity(request.getQuantity())
                            .price(request.getPrice())
                            .name(request.getName())
                            .build();
                    build.setCreatedBy(request.getCreatedBy());
                    build.setCreatedAt(String.valueOf(LocalDateTime.now()));
                    build.setUpdatedAt(String.valueOf(LocalDateTime.now()));
                    repository.save(build);
                    BookResponse dto = mapper.toDto(build);
                    dto.setBook(build);
                    return dto;
                }
            }
            throw new NotFoundException(createdBy + ": Didn't found");
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }

    }
}
