package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.ResponseMessage;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartRequest;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.mapper.CartMapper;
import com.mokhir.dev.BookShop.exceptions.*;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.BookRepository;
import com.mokhir.dev.BookShop.repository.interfaces.CartRepository;
import com.mokhir.dev.BookShop.service.interfaces.CartServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements CartServiceInterface {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final CartMapper cartMapper;
    private final JwtProvider jwtProvider;

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    /**
     * Adds a book to the user's cart.
     *
     * @param cartRequest The CartRequest object containing information about the book to be added to the cart
     * @return A CartResponse object containing information about the added item in the cart
     * @throws NotFoundException if the requested book is not found or is not available
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    @Transactional
    public CartResponse addToCart(CartRequest cartRequest) {
        try {
            // Get the ID and quantity of the book to be added to the cart
            Long bookId = cartRequest.getBookId();
            Integer inCartQuantity = cartRequest.getQuantity();

            // Find the book in the repository by its ID
            Book book = bookRepository.findById(bookId).orElseThrow(() ->
                    new NotFoundException("Book with current id:%d not found".formatted(bookId)));

            // Check if the book is available and active
            if (book.getQuantity() == 0 || !book.getActive()) {
                throw new EntityNotLeftException(
                        "Book with current id:%d did not left or not active".formatted(bookId));
            }

            // Find all items in the user's cart
            List<Cart> allByCreatedBy = cartRepository.findAllByCreatedBy(jwtProvider.getCurrentUser());

            // Check if adding the requested quantity of the book exceeds availability
            if (!checkAvailabilityQuantityBook(allByCreatedBy, book, inCartQuantity)) {
                throw new LimitCrowdedException("Limit crowded, exist books:%d, you want to add:%d, wasted:%d"
                        .formatted(book.getQuantity(),
                                inCartQuantity,
                                inCartQuantity - (book.getQuantity() - countBooksInCard(allByCreatedBy, book))));
            }

            // Get an existing cart item if it already exists, otherwise create a new one
            Cart entity = getExistCart(cartRequest);

            // Save the cart item to the database
            cartRepository.save(entity);

            // Map the cart item entity to a CartResponse object and return it
            return cartMapper.toDto(entity);
        } catch (NotFoundException e) {
            // Log the exception and re-throw NotFoundException with the original message
            logger.error("Error adding book to cart: {}", e.getMessage());
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            // Log the exception and wrap any other exceptions in a DatabaseException and throw it
            logger.error("Error accessing database: {}", e.getMessage());
            throw new DatabaseException("addToCart: " + e.getMessage());
        }
    }

    /**
     * Retrieves an existing cart item or creates a new one based on the provided CartRequest object.
     *
     * @param cartRequest The CartRequest object containing information about the book to be added to the cart
     * @return A Cart object representing the existing or new cart item
     * @throws DatabaseException if there is an error accessing the database
     */
    private Cart getExistCart(CartRequest cartRequest) {
        try {
            // Find all cart items created by the current user
            List<Cart> allByCreatedBy = cartRepository.findAllByCreatedBy(jwtProvider.getCurrentUser());

            // Check if there is already a cart item for the requested book
            Optional<Cart> existCart = allByCreatedBy
                    .stream()
                    .filter(cart -> cart.getBook().getId().equals(cartRequest.getBookId()))
                    .findFirst();

            // Find the book in the repository by its ID
            Book book = bookRepository.findById(cartRequest.getBookId()).orElseThrow(() ->
                    new NotFoundException("Book with ID: %d not found".formatted(cartRequest.getBookId())));

            if (existCart.isEmpty()) {
                // If no existing cart item is found, create a new one
                Cart newCart = cartMapper.toEntity(cartRequest);
                newCart.setBook(book);
                newCart.setTotalPrice(newCart.getQuantity() * book.getPrice());
                return newCart;
            }

            // If an existing cart item is found, update its quantity and total price
            Cart cart = existCart.get();
            Integer quantityAllBooks = cart.getQuantity();
            cart.setQuantity(quantityAllBooks + cartRequest.getQuantity());
            cart.setTotalPrice(quantityAllBooks * book.getPrice());
            return cart;
        } catch (Exception e) {
            // Log the exception and wrap it in a DatabaseException and throw it
            logger.error("Error retrieving existing cart or creating a new one: {}", e.getMessage());
            throw new DatabaseException("getExistCart: " + e.getMessage());
        }
    }

    /**
     * Removes a specified quantity of books from the cart based on the provided CartRequest object.
     *
     * @param cartRequest The CartRequest object containing information about the cart item to be updated
     * @return A CartResponse object containing information about the updated cart item
     * @throws NotFoundException if the cart item with the specified ID is not found
     * or if the current user does not own the cart item
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    @Transactional
    public CartResponse removeFromCart(CartRequest cartRequest) {
        try {
            // Get the quantity to be removed from the cart
            Integer wasteQuantity = cartRequest.getQuantity();
            // Get the ID of the cart item to be updated
            Long cartId = cartRequest.getCartId();
            // Find the cart item in the database by its ID
            Cart cart = cartRepository.findById(cartId).orElseThrow(() ->
                    new NotFoundException("Cart with ID %d not found".formatted(cartId)));
            // Check if the current user owns the cart item
            if (!jwtProvider.getCurrentUser().equals(cart.getCreatedBy())) {
                throw new CurrentUserNotOwnCurrentEntityException(
                        "Current user does not own the specified cart item");
            }
            // Get the current quantity of books in the cart
            Integer dbQuantity = cart.getQuantity();
            // Update the quantity in the cart if the requested quantity is valid
            if (wasteQuantity > 0 && wasteQuantity <= dbQuantity) {
                cart.setQuantity(dbQuantity - wasteQuantity);
            }
            // Save the changes to the database
            Cart savedCart = cartRepository.save(cart);
            // Map the updated cart item to a CartResponse object and return it
            return cartMapper.toDto(savedCart);
        } catch (NotFoundException e) {
            // Re-throw NotFoundException with the original message
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            // Log the exception and wrap it in a DatabaseException and throw it
            logger.error("Error removing book from cart: {}", e.getMessage());
            throw new DatabaseException("removeFromCart: " + e.getMessage());
        }
    }

    /**
     * Retrieves information about a specified cart item based on the provided CartRequest object.
     *
     * @param cartRequest The CartRequest object containing the ID of the cart item to be retrieved
     * @return A CartResponse object containing information about the retrieved cart item
     * @throws NotFoundException if the cart item with the specified ID is not found
     * or if the current user does not own the cart item
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public CartResponse getCart(CartRequest cartRequest) {
        try {
            // Get the ID of the cart item to be retrieved
            Long cartId = cartRequest.getCartId();
            // Find the cart item in the database by its ID
            Cart cart = cartRepository.findById(cartId).orElseThrow(() ->
                    new NotFoundException("Cart with ID %d not found".formatted(cartId)));
            // Check if the current user owns the cart item
            if (!jwtProvider.getCurrentUser().equals(cart.getCreatedBy())) {
                throw new CurrentUserNotOwnCurrentEntityException(
                        "Current user does not own the specified cart item");
            }
            // Map the cart item to a CartResponse object and return it
            return cartMapper.toDto(cart);
        } catch (NotFoundException e) {
            // Re-throw NotFoundException with the original message
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            // Log the exception and wrap it in a DatabaseException and throw it
            logger.error("Error retrieving cart item: {}", e.getMessage());
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Retrieves a page of cart items belonging to the current user.
     *
     * @param pageable The Pageable object specifying the pagination parameters
     * @return A ResponseMessage object containing a page of CartResponse objects and additional information
     * @throws NotFoundException if the current user does not have any cart items
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public ResponseMessage<Page<CartResponse>> getAllCarts(Pageable pageable) {
        try {
            // Get the username of the current user
            String currentUser = jwtProvider.getCurrentUser();
            // Find all cart items belonging to the current user
            List<Cart> cartList = cartRepository.findAllByCreatedBy(currentUser);
            // Check if the cart list is empty
            Page<Cart> cartPage = getCarts(pageable, cartList, currentUser);
            // Map the cart Page object to a Page of CartResponse objects
            Page<CartResponse> mappedPage = cartPage.map(cartMapper::toDto);
            // Calculate the total price of all carts
            Integer totalPriceAllCarts = cartList.stream().map(Cart::getTotalPrice).reduce(0, Integer::sum);
            // Create a ResponseMessage object and set its attributes
            ResponseMessage<Page<CartResponse>> responseMessage = new ResponseMessage<>();
            responseMessage.setMessage("Total price of all carts: " + totalPriceAllCarts);
            responseMessage.setEntities(mappedPage);
            responseMessage.setCurrentUser(currentUser);
            // Return the ResponseMessage object
            return responseMessage;
        } catch (NotFoundException e) {
            // Re-throw NotFoundException with the original message
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            // Log the exception and wrap it in a DatabaseException and throw it
            logger.error("Error retrieving all carts: {}", e.getMessage());
            throw new DatabaseException(e.getMessage());
        }
    }

    private static Page<Cart> getCarts(Pageable pageable, List<Cart> cartList, String currentUser) {
        if (cartList.isEmpty()) {
            throw new NotFoundException("Current user %s doesn't have any carts".formatted(currentUser));
        }
        // Create a PageRequest object for pagination
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        // Create a Page object for the cart list
        return new PageImpl<>(cartList, pageRequest, cartList.size());
    }

    /**
     * Deletes a cart item based on the provided CartRequest object.
     *
     * @param cartRequest The CartRequest object containing the ID of the cart item to be deleted
     * @return A ResponseMessage object containing information about the deleted cart item
     * @throws NotFoundException if the cart item with the specified ID is not found
     *                            or if the current user does not own the cart item
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public ResponseMessage<CartResponse> deleteCart(CartRequest cartRequest) {
        try {
            // Get the username of the current user
            String currentUser = jwtProvider.getCurrentUser();
            // Get the ID of the cart item to be deleted from the request
            Long cartId = cartRequest.getCartId();
            // Find the cart item in the database by its ID
            Cart cart = cartRepository.findById(cartId).orElseThrow(
                    () -> new NotFoundException("Cart with ID %d not found".formatted(cartId)));
            // Check if the cart item belongs to the current user
            if (!cart.getCreatedBy().equals(currentUser)) {
                throw new NotFoundException("Current user %s does not own any cart items".formatted(currentUser));
            }
            // Delete the cart item from the database
            cartRepository.deleteById(cartId);
            // Create a ResponseMessage object and set its attributes
            ResponseMessage<CartResponse> responseMessage = new ResponseMessage<>();
            responseMessage.setCurrentUser(currentUser);
            responseMessage.setEntities(cartMapper.toDto(cart));
            responseMessage.setMessage("Cart deleted successfully, with ID %d".formatted(cartId));
            // Return the ResponseMessage object
            return responseMessage;
        } catch (NotFoundException e) {
            // Re-throw NotFoundException with the original message
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            // Log the exception and wrap it in a DatabaseException and throw it
            logger.error("Error deleting cart: {}", e.getMessage());
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Deletes all cart items belonging to the current user.
     *
     * @return A ResponseMessage object containing information about the deleted cart items
     * @throws NotFoundException if the current user does not own any cart items
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    @Transactional
    public ResponseMessage<List<CartResponse>> deleteAllCarts() {
        try {
            // Get the username of the current user
            String currentUser = jwtProvider.getCurrentUser();
            // Find all cart items belonging to the current user
            List<Cart> allByCreatedBy = cartRepository.findAllByCreatedBy(currentUser);
            // Check if the current user has any cart items
            if (allByCreatedBy.isEmpty()) {
                throw new NotFoundException("Current user %s doesn't have any carts".formatted(currentUser));
            }
            // Delete all cart items belonging to the current user
            cartRepository.deleteAllByCreatedBy(currentUser);
            // Map the deleted cart items to CartResponse objects
            List<CartResponse> list = allByCreatedBy.stream().map(cartMapper::toDto).toList();
            // Create a ResponseMessage object and set its attributes
            ResponseMessage<List<CartResponse>> responseMessage = new ResponseMessage<>();
            responseMessage.setMessage("All carts deleted");
            responseMessage.setEntities(list);
            responseMessage.setCurrentUser(currentUser);
            // Return the ResponseMessage object
            return responseMessage;
        } catch (NotFoundException e) {
            // Re-throw NotFoundException with the original message
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            // Log the exception and wrap it in a DatabaseException and throw it
            logger.error("Error deleting all carts: {}", e.getMessage());
            throw new DatabaseException("deleteAllCarts: " + e.getMessage());
        }
    }

    /**
     * Checks if the requested quantity of a book is available in the user's cart.
     *
     * @param allByCreatedBy A list of cart items belonging to the user
     * @param book            The book to be checked for availability
     * @param expectedCount   The expected quantity of the book
     * @return true if the requested quantity is available, otherwise false
     * @throws DatabaseException if there is an error accessing the database
     */
    private boolean checkAvailabilityQuantityBook(List<Cart> allByCreatedBy, Book book, Integer expectedCount) {
        try {
            // Check if the list of cart items is empty and the expected count
            // is less than or equal to the book's quantity
            if (allByCreatedBy.isEmpty() && expectedCount <= book.getQuantity()) {
                return true;
            }
            // Throw an exception if the expected count is less than zero
            if (expectedCount < 0) {
                throw new IncorrectValueException("Expected value must be higher than zero!");
            }
            // Count the number of books in the user's cart
            Integer countBooksInCart = countBooksInCard(allByCreatedBy, book);
            // Return true if the sum of the counted books and
            // the expected count is less than or equal to the book's quantity
            return countBooksInCart + expectedCount <= book.getQuantity();
        } catch (Exception e) {
            // Log the exception and wrap it in a DatabaseException and throw it
            logger.error("Error checking book availability: {}", e.getMessage());
            throw new DatabaseException("checkAvailabilityQuantityBook: " + e.getMessage());
        }
    }

    /**
     * Counts the total quantity of a book in the user's cart.
     *
     * @param allByCreatedBy A list of cart items belonging to the user
     * @param book            The book to be counted
     * @return The total quantity of the book in the user's cart
     * @throws DatabaseException if there is an error accessing the database
     */
    private Integer countBooksInCard(List<Cart> allByCreatedBy, Book book) {
        try {
            // Filter the cart items by the book ID
            List<Cart> cartList = allByCreatedBy.stream()
                    .filter(cart -> cart.getBook().getId().equals(book.getId()))
                    .toList();
            // Return zero if the filtered list is empty
            if (cartList.isEmpty()) {
                return 0;
            }
            // Sum the quantities of the filtered cart items
            int totalQuantity = cartList.stream()
                    .mapToInt(Cart::getQuantity)
                    .sum();
            logger.info("Total quantity of book {} in cart: {}", book.getId(), totalQuantity);
            return totalQuantity;
        } catch (Exception e) {
            // Log the exception and wrap it in a DatabaseException and throw it
            logger.error("Error counting books in cart: {}", e.getMessage());
            throw new DatabaseException("countBooksInCard: " + e.getMessage());
        }
    }

    /**
     * Removes cart items by their IDs.
     *
     * @param cartList A list of cart item IDs to be removed
     * @throws DatabaseException if there is an error accessing the database
     */
    @Transactional
    public void removeFromCart(List<Long> cartList) {
        try {
            // Delete cart items by their IDs
            cartRepository.deleteAllById(cartList);
        } catch (Exception e) {
            // Log the exception and wrap it in a DatabaseException and throw it
            logger.error("Error removing cart items: {}", e.getMessage());
            throw new DatabaseException("removeFromCart: " + e.getMessage());
        }
    }
}
