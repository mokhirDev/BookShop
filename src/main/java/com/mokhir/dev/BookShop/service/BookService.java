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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


@Service
@RequiredArgsConstructor
public class BookService
        implements EntityServiceInterface<Book, BookRequest, BookResponse, String> {
    private final BookRepository repository;
    private final BookMapper mapper;
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final BookRepository bookRepository;
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    /**
     * Retrieves information about a book by its identifier.
     *
     * @param id The identifier of the book
     * @return A BookResponse object containing information about the book
     * @throws NotFoundException if the book with the specified identifier is not found
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public BookResponse getById(String id) {
        try {
            // Parse the identifier to a Long
            Long realId = Long.valueOf(id);

            // Find the book by its ID in the repository
            Optional<Book> byId = repository.findById(realId);

            // Check if the book exists
            if (byId.isEmpty()) {
                // Log a warning since the book was not found
                logger.warn("Book with ID {} not found", realId);

                // Throw a NotFoundException with an appropriate message
                throw new NotFoundException(realId + ": not found");
            }

            // Retrieve the book from the Optional
            Book book = byId.get();

            // Map the book entity to a BookResponse object and return it
            return mapper.toDto(book);
        } catch (NotFoundException ex) {
            // Log the exception
            logger.error("Error retrieving book: {}", ex.getMessage());

            // Re-throw NotFoundException with the original message
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Log the exception
            logger.error("Error accessing database: {}", ex.getMessage());

            // Wrap any other exceptions in a DatabaseException and throw it
            throw new DatabaseException(ex.getMessage());
        }
    }


    /**
     * Retrieves a page of books based on the provided Pageable object.
     *
     * @param pageable The Pageable object specifying the pagination parameters
     * @return A Page object containing a list of BookResponse objects
     * @throws NotFoundException if no books are found
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public Page<BookResponse> findAll(Pageable pageable) {
        try {
            // Retrieve all books from the repository based on the provided Pageable object
            Page<Book> all = repository.findAll(pageable);

            // Check if any books were found
            if (all.isEmpty()) {
                // Log a warning since no books were found
                logger.warn("No books found");

                // Throw a NotFoundException with an appropriate message
                throw new NotFoundException("Books not found");
            }

            // Filter out inactive books
            List<Book> activeBooks = all.stream().filter(Book::getActive).toList();

            // Map Book objects to BookResponse objects
            List<BookResponse> responseList = activeBooks.stream().map(mapper::toDto).collect(toList());

            // Return a new Page object with the filtered and mapped data
            return new PageImpl<>(responseList, pageable, all.getTotalElements());
        } catch (NotFoundException ex) {
            // Log the exception
            logger.error("Error finding books: {}", ex.getMessage());

            // Re-throw NotFoundException with the original message
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Log the exception
            logger.error("Error accessing database: {}", ex.getMessage());

            // Wrap any other exceptions in a DatabaseException and throw it
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Retrieves a page of books created by a specific user.
     *
     * @param pageable  The Pageable object specifying the pagination parameters
     * @param createdBy The username of the user who created the books
     * @return A Page object containing a list of BookResponse objects created by the specified user
     * @throws NotFoundException   if no books are found for the specified user
     * @throws DatabaseException   if there is an error accessing the database
     */
    public Page<BookResponse> findAllBooksByCreatedBy(Pageable pageable, String createdBy) {
        try {
            // Retrieve a page of books created by the specified user
            Page<Book> allBooksByCreatedBy = repository.findAllBooksByCreatedBy(createdBy, pageable);
            // Map Book objects to BookResponse objects
            Page<BookResponse> bookResponses = allBooksByCreatedBy.map(mapper::toDto);

            // Log successful retrieval
            logger.info("Retrieved {} books created by user '{}'", bookResponses.getTotalElements(), createdBy);

            return bookResponses;
        } catch (NotFoundException ex) {
            // Log the exception
            logger.error("Error finding books created by user '{}': {}", createdBy, ex.getMessage());

            // Re-throw NotFoundException with the original message
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Log the exception
            logger.error("Error accessing database: {}", ex.getMessage());

            // Wrap any other exceptions in a DatabaseException and throw it
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Registers a new book based on the provided BookRequest object.
     *
     * @param request The BookRequest object containing information about the book to be registered
     * @return A BookResponse object containing information about the registered book
     * @throws NotFoundException if the requested operation cannot be completed due to a missing resource
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public BookResponse register(BookRequest request) {
        try {
            // Map the BookRequest object to a Book entity
            Book entity = mapper.toEntity(request);

            // Log the registration of the book
            logger.info("Registering a new book: {}", entity);

            // Save the book entity to the database
            Book save = repository.save(entity);

            // Log the successful registration
            logger.info("Book registered successfully: {}", save);

            // Map the saved book entity to a BookResponse object and return it
            return mapper.toDto(save);
        } catch (NotFoundException ex) {
            // Log the exception
            logger.error("Error registering book: {}", ex.getMessage());

            // Re-throw NotFoundException with the original message
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Log the exception
            logger.error("Error registering book: {}", ex.getMessage());

            // Wrap any other exceptions in a DatabaseException and throw it
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Removes a book based on the provided BookRequest object.
     *
     * @param request The BookRequest object containing the ID of the book to be removed
     * @return A BookResponse object containing information about the removed book
     * @throws NotFoundException if the book with the specified ID is not found,
     * is not active, or does not belong to the current user
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public BookResponse remove(BookRequest request) {
        try {
            logger.info("Removing book with ID: {}", request.getId());

            // Get the username of the current user from the JWT token
            String createdBy = jwtProvider.getCurrentUser();

            // Get the ID of the book to be removed from the request
            Long id = request.getId();
            if (id == null) {
                throw new NotFoundException("Book ID must not be null");
            }

            // Find the book in the database by its ID
            Book book = repository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Book not found"));

            // Check if the book is active
            if (!book.getActive()) {
                throw new NotFoundException("Book is not active");
            }

            // Check if the book belongs to the current user
            if (!book.getCreatedBy().equals(createdBy)) {
                throw new NotFoundException("Book does not belong to the current user");
            }

            // Set the book as inactive
            book.setActive(false);

            // Save the changes to the database
            repository.save(book);

            // Map the book entity to a BookResponse object and return it
            BookResponse removedBook = mapper.toDto(book);
            logger.info("Book removed successfully: {}", removedBook);
            return removedBook;
        } catch (NotFoundException ex) {
            // Re-throw NotFoundException with the original message
            logger.error("Error removing book: {}", ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Wrap any other exceptions in a DatabaseException and throw it
            logger.error("Error removing book: {}", ex.getMessage());
            throw new DatabaseException("remove: BookService: " + ex.getMessage());
        }
    }


    /**
     * Updates a book based on the provided BookRequest object.
     *
     * @param request The BookRequest object containing the information to update the book
     * @return A BookResponse object containing information about the updated book
     * @throws NotFoundException if the book with the specified ID is not found or does not belong to the current user
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public BookResponse update(BookRequest request) {
        try {
            logger.info("Updating book with ID: {}", request.getId());

            // Get the ID of the book to be updated from the request
            Long id = request.getId();
            if (id == null) {
                throw new NotFoundException("Book ID must not be null");
            }
            // Find the book in the database by its ID
            Book book = bookRepository.findById(id).orElseThrow(
                    () -> new NotFoundException(String.format("Book not found with ID: %d", id)));

            // Get the username of the current user from the JWT token
            String currentUser = jwtProvider.getCurrentUser();

            // Check if the book belongs to the current user
            if (book.getCreatedBy().equals(currentUser)) {
                // Update the book entity with the information from the request
                mapper.updateFromDto(request, book);
                // Save the changes to the database
                repository.save(book);
                // Map the updated book entity to a BookResponse object and return it
                BookResponse updatedBook = mapper.toDto(book);
                logger.info("Book updated successfully: {}", updatedBook);
                return updatedBook;
            }

            // If the book does not belong to the current user, throw an exception
            String errorMessage = String.format("Current user %s does not own the book with ID: %d", currentUser, id);
            logger.error(errorMessage);
            throw new NotFoundException(errorMessage);
        } catch (NotFoundException ex) {
            // Re-throw NotFoundException with the original message
            logger.error("Error updating book: {}", ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Wrap any other exceptions in a DatabaseException and throw it
            logger.error("Error updating book: {}", ex.getMessage());
            throw new DatabaseException(ex.getMessage());
        }
    }


    /**
     * Adds a new book based on the provided BookRequest object.
     *
     * @param request The BookRequest object containing information about the book to be added
     * @return A BookResponse object containing information about the added book
     * @throws NotFoundException if the current user is not found or if a book with the same name already exists
     * @throws DatabaseException if there is an error accessing the database
     */
    public BookResponse addBook(BookRequest request) {
        try {
            logger.info("Adding a new book");

            // Get the username of the current user from the JWT token
            String createdBy = jwtProvider.getCurrentUser();

            // Check if the username is not blank
            if (!createdBy.isBlank()) {
                // Find all books created by the current user
                List<Book> booksByCreatedBy = repository.findAllBooksByCreatedBy(createdBy);

                // Iterate through each book to check if a book with the same name already exists
                for (Book book : booksByCreatedBy) {
                    String bookName = book.getName().replaceAll(" ", "").toLowerCase();
                    String newBookName = request.getName().replaceAll(" ", "").toLowerCase();

                    if (bookName.equals(newBookName)) {
                        logger.error("Book with name {} already exists", bookName);
                        throw new DatabaseException(bookName + ": Book already exists");
                    }
                }

                // Find the user by username
                User userByUsername = userService.findUserByUsername(createdBy);

                // If the user exists, create a new book entity and save it to the database
                if (userByUsername != null) {
                    Book entity = mapper.toEntity(request);
                    entity.setCreatedBy(request.getCreatedBy());
                    entity.setCreatedAt(String.valueOf(LocalDateTime.now()));
                    entity.setUpdatedAt(String.valueOf(LocalDateTime.now()));
                    repository.save(entity);

                    // Map the new book entity to a BookResponse object and return it
                    BookResponse bookResponse = mapper.toDto(entity);
                    logger.info("New book added successfully: {}", bookResponse);
                    return bookResponse;
                }
            }

            // If the current user is not found, throw a NotFoundException
            logger.error("User {} not found", createdBy);
            throw new NotFoundException(createdBy + ": User not found");
        } catch (NotFoundException ex) {
            // Re-throw NotFoundException with the original message
            logger.error("Error adding book: {}", ex.getMessage());
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            // Wrap any other exceptions in a DatabaseException and throw it
            logger.error("Error adding book: {}", ex.getMessage());
            throw new DatabaseException(ex.getMessage());
        }
    }


    /**
     * Retrieves a book by its ID.
     *
     * @param request The BookRequest object containing the ID of the book to be retrieved
     * @return A BookResponse object containing information about the retrieved book
     * @throws NotFoundException if the book with the specified ID does not exist, is not active, or does not belong to the current user
     */
    public BookResponse getBookById(BookRequest request) {
        try {
            logger.info("Fetching book by ID: {}", request.getId());

            // Get the ID of the book to be retrieved from the request
            Long id = request.getId();
            if (id == null) {
                throw new NotFoundException("Book ID must not be null");
            }

            // Find the book in the database by its ID
            Book book = repository.findById(id).orElseThrow(() -> new NotFoundException("Book not found"));

            // Check if the book is active
            if (!book.getActive()) {
                throw new NotFoundException("Book is not active");
            }

            // Get the username of the user who created the book
            String createdBy = book.getCreatedBy();

            // Get the username of the current user from the JWT token
            String currentUser = jwtProvider.getCurrentUser();

            // Check if the book belongs to the current user
            if (!createdBy.equals(currentUser)) {
                throw new NotFoundException("Book does not belong to the current user");
            }

            logger.info("Book fetched successfully");

            // Map the book entity to a BookResponse object and return it
            return mapper.toDto(book);
        } catch (NotFoundException ex) {
            logger.error("Error fetching book: {}", ex.getMessage());
            // Re-throw NotFoundException with the original message
            throw new NotFoundException(ex.getMessage());
        }
    }


    /**
     * Reduces the quantity of books based on the provided cart items.
     *
     * @param cartList The list of cart items containing book IDs and quantities
     * @return A list of BookResponse objects representing the updated book quantities
     * @throws DatabaseException if there is an error accessing the database
     */
    public List<BookResponse> minusBookQuantity(List<Cart> cartList) {
        try {
            logger.info("Start reducing book quantities");

            // Fetch the books from the database based on the provided cart items
            List<Book> list = cartList.stream()
                    .map(cart -> bookRepository.findById(cart.getBook().getId()).get())
                    .toList();

            // Adjust the quantity of each book based on the cart items
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setQuantity(list.get(i).getQuantity() - cartList.get(i).getQuantity());
            }

            // Save the updated book quantities to the database
            List<Book> books = repository.saveAll(list);

            // Map the updated book quantities to BookResponse objects
            List<BookResponse> list1 = cartList.stream()
                    .map(cart -> BookResponse.builder()
                            .name(cart.getBook().getName())
                            .price(cart.getBook().getPrice())
                            .quantity(cart.getQuantity())
                            .id(cart.getBook().getId())
                            .build())
                    .toList();

            logger.info("Successfully reduced book quantities");

            return list1;
        } catch (Exception ex) {
            logger.error("Error reducing book quantities: {}", ex.getMessage());
            throw new DatabaseException("Error reducing book quantities: " + ex.getMessage());
        }
    }
}
