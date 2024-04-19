package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.entity.User;
import com.mokhir.dev.BookShop.aggregation.mapper.BookMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import com.mokhir.dev.BookShop.repository.interfaces.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final BookRepository bookRepository;

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
            List<Book> list = all.stream().filter(book -> book.getActive().equals(true)).toList();
            List<BookResponse> responseList = list.stream().map(mapper::toDto).toList();
            return new PageImpl<>(responseList, pageable, all.getTotalElements());
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    public Page<BookResponse> findAllBooksByCreatedBy(Pageable pageable, String createdBy) {
        try {
            Page<Book> allBooksByCreatedBy = repository.findAllBooksByCreatedBy(createdBy, pageable);
            return allBooksByCreatedBy.map(mapper::toDto);
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
            if (book.getActive().equals(false)) {
                throw new NotFoundException("Book not active");
            }
            if (!book.getCreatedBy().equals(createdBy)) {
                throw new NotFoundException("Book does not belong to the current user");
            }
            book.setActive(false);
            repository.save(book);
            return mapper.toDto(book);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException("remove: BookService: " + ex.getMessage());
        }
    }

    @Override
    public BookResponse update(BookRequest request) {
        try {
            Long id = request.getId();
            if (id == null) {
                throw new NotFoundException("Book id must not be null");
            }
            Book book = bookRepository.findById(id).orElseThrow(
                    () -> new NotFoundException("Book did not found, with id:%d ".formatted(id)));
            String currentUser = jwtProvider.getCurrentUser();
            if (book.getCreatedBy().equals(currentUser)) {
                mapper.updateFromDto(request, book);
                repository.save(book);
                Book entity = mapper.toEntity(request);
                return mapper.toDto(entity);
            }
            throw new NotFoundException
                    ("Current user:%s doesn't owner book with id:%d"
                            .formatted(currentUser, id));
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
                    return mapper.toDto(entity);
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
            if (book.getActive().equals(false)) {
                throw new NotFoundException("Book not active");
            }
            String createdBy = book.getCreatedBy();
            String currentUser = jwtProvider.getCurrentUser();
            if (!createdBy.equals(currentUser)) {
                throw new NotFoundException("Book does not belong to the current user");
            }
            return mapper.toDto(book);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        }
    }

    public List<BookResponse> minusBookQuantity(List<Cart> cartList) {
        try {
            System.out.println("11111 " + cartList);
            List<Book> list = cartList.stream().map(cart -> bookRepository.findById(cart.getBook().getId()).get()).toList();
            System.out.println("22222 " + list);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setQuantity(list.get(i).getQuantity() - cartList.get(i).getQuantity());
            }
            System.out.println("33333 " + list);
            List<Book> books = repository.saveAll(list);
            System.out.println("44444 " + books);
            List<BookResponse> list1 = cartList.stream()
                    .map(cart ->
                            BookResponse.builder()
                                    .name(cart.getBook().getName())
                                    .price(cart.getBook().getPrice())
                                    .quantity(cart.getQuantity())
                                    .id(cart.getBook().getId())
                                    .build()).toList();
            System.out.println("555555 " + list1);
            return list1;
        } catch (Exception ex) {
            throw new DatabaseException(" minusBookQuantity: " + ex.getMessage());
        }
    }
}
