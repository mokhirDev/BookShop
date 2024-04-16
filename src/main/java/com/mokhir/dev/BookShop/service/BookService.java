package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import com.mokhir.dev.BookShop.aggregation.mapper.BookMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
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
        implements EntityServiceInterface<Book, BookRequest, BookResponse, String> {
    private final BookRepository repository;
    private final BookMapper mapper;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Override
    public BookResponse getById(String id) {
        try {
            Long realId = Long.valueOf(id);
            Optional<Book> byId = repository.findById(realId);
            if (byId.isEmpty()) {
                throw new NotFoundException(realId + ": not found");
            }
            Book book = byId.get();
            return mapper.toDto(book);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }

    }

    @Override
    public Page<BookResponse> findAll(Pageable pageable) {
        try {
            Page<Book> all = repository.findAll(pageable);
            if (all.isEmpty()) {
                throw new NotFoundException("Book didn't found");
            }
            return all.map(book -> {
                return BookResponse
                        .builder()
                        .book(book)
                        .build();
            });
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    public Page<BookResponse> findAllBooksByCreatedBy(Pageable pageable, String createdBy) {
        try {
            Page<Book> allBooksByCreatedBy = repository.findAllBooksByCreatedBy(createdBy, pageable);
            return allBooksByCreatedBy.map(book -> {
                return BookResponse.builder().book(book).build();
            });
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public BookResponse register(BookRequest request) {
        try {
            Book entity = mapper.toEntity(request);
            Book save = repository.save(entity);
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
            String createdBy = jwtProvider.getCurrentUser();
            Long id = request.getId();
            if (id == null) {
                throw new NotFoundException("Book id must not be null");
            }
            Book book = repository.findById(id).orElseThrow(() -> new NotFoundException("Book did not found"));
            if (!book.getCreatedBy().equals(createdBy)) {
                throw new NotFoundException("Book does not belong to the current user");
            }
            repository.delete(book);
            return BookResponse.builder().book(book).build();
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
            Optional<Book> byId = repository.findBooksByCreatedBy(createdBy);
            if (byId.isPresent()) {
                mapper.updateFromDto(request, byId.get());
                Book entity = mapper.toEntity(request);
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
            String createdBy = jwtProvider.getCurrentUser();
            if (!createdBy.isBlank()) {
                List<Book> bookByCreatedBy = repository.findAllBooksByCreatedBy(createdBy);
                for (Book book : bookByCreatedBy) {
                    String bookName = book.getName().replaceAll(" ", "").toLowerCase();
                    String newBookName = request.getName().replaceAll(" ", "").toLowerCase();
                    if (bookName.equals(newBookName)) {
                        throw new DatabaseException(bookName + ": Book already exists");
                    }
                }
                User userByUsername = userService.findUserByUsername(createdBy);
                if (userByUsername != null) {
                    Book entity = mapper.toEntity(request);
                    entity.setCreatedBy(request.getCreatedBy());
                    entity.setCreatedAt(String.valueOf(LocalDateTime.now()));
                    entity.setUpdatedAt(String.valueOf(LocalDateTime.now()));
                    repository.save(entity);
                    BookResponse dto = mapper.toDto(entity);
                    dto.setBook(entity);
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

    public BookResponse getBookById(BookRequest request) {
        try {
            Long id = request.getId();
            if (id == null) {
                throw new NotFoundException("Book id must not be null");
            }
            Book book = repository.findById(id).orElseThrow(() -> new NotFoundException("Book did not exist"));
            String createdBy = book.getCreatedBy();
            String currentUser = jwtProvider.getCurrentUser();
            if (!createdBy.equals(currentUser)) {
                throw new NotFoundException("Book does not belong to the current user");
            }
            return BookResponse.builder().book(book).build();
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        }
    }
}
