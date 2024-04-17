package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.ResponseMessage;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartRequest;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.mapper.CartMapper;
import com.mokhir.dev.BookShop.exceptions.CurrentUserNotOwnCurrentEntityException;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.LimitCrowdedException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.BookRepository;
import com.mokhir.dev.BookShop.repository.interfaces.CartRepository;
import com.mokhir.dev.BookShop.service.interfaces.CartServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CartService implements CartServiceInterface {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final CartMapper cartMapper;
    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public CartResponse addToCart(CartRequest cartRequest) {
        try {
            Long bookId = cartRequest.getBookId();
            Integer inCartQuantity = cartRequest.getQuantity();
            Book book = bookRepository.findById(bookId).orElseThrow(() ->
                    new NotFoundException("Book with current id:%d not found".formatted(bookId)));
            List<Cart> allByCreatedBy = cartRepository.findAllByCreatedBy(jwtProvider.getCurrentUser());
            if (!checkAvailabilityQuantityBook(allByCreatedBy, book, inCartQuantity)) {
                throw new LimitCrowdedException("Limit crowded, books:%d, you want to add:%d, wasted:%d"
                        .formatted(book.getQuantity(),
                                inCartQuantity,
                                inCartQuantity - (book.getQuantity() - countBooksInCard(allByCreatedBy, book))));
            }
            Cart entity = cartMapper.toEntity(cartRequest);
            entity.setBook(book);
            cartRepository.save(entity);
            return cartMapper.toDto(entity);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(CartRequest cartRequest) {
        try {
            Integer wasteQuantity = cartRequest.getQuantity();
            Long cardId = cartRequest.getCartId();
            Cart cart = cartRepository.findById(cardId).orElseThrow(() ->
                    new NotFoundException("Card with current id:%d not found".formatted(cardId)));
            if (jwtProvider.getCurrentUser() != cart.getCreatedBy()) {
                throw new CurrentUserNotOwnCurrentEntityException(
                        "Current cart:%s not own current user".formatted(cart.getCreatedBy()));
            }
            Integer dbQuantity = cart.getQuantity();
            if (wasteQuantity > 0 && wasteQuantity <= dbQuantity) {
                cart.setQuantity(dbQuantity - wasteQuantity);
            }
            Cart save = cartRepository
                    .save(cartMapper.toEntity(cartRequest));
            return cartMapper.toDto(save);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public CartResponse getCart(CartRequest cartRequest) {
        try {
            Long cardId = cartRequest.getCartId();
            Cart cart = cartRepository.findById(cardId).orElseThrow(() ->
                    new NotFoundException("Card with current id:%d not found".formatted(cardId)));
            if (jwtProvider.getCurrentUser() != cart.getCreatedBy()) {
                throw new CurrentUserNotOwnCurrentEntityException(
                        "Current cart:%s not own current user".formatted(cart.getCreatedBy()));
            }
            return cartMapper.toDto(cart);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public Page<CartResponse> getAllCarts(Pageable pageable) {
        try {
            String currentUser = jwtProvider.getCurrentUser();
            List<Cart> cartList = cartRepository.findAllByCreatedBy(currentUser);
            if (cartList.isEmpty()) {
                throw new NotFoundException("Current user:%s doesn't have any cards".formatted(currentUser));
            }
            PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            Page<Cart> cartPage = new PageImpl<>(cartList, pageRequest, cartList.size());
            return cartPage.map(cartMapper::toDto);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public CartResponse deleteCart(CartRequest cartRequest) {
        try {
            String currentUser = jwtProvider.getCurrentUser();
            Long cartId = cartRequest.getCartId();
            Cart cart = cartRepository.findById(cartId).orElseThrow(
                    () -> new NotFoundException("Cart with current id:%d not found".formatted(cartId)));
            if (!cart.getCreatedBy().equals(currentUser)) {
                throw new NotFoundException("Current user:%s doesn't have any cards".formatted(currentUser));
            }
            cartRepository.deleteById(cartId);
            return cartMapper.toDto(cart);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public ResponseMessage deleteAllCarts(CartRequest cartRequest) {
        try {
            String currentUser = jwtProvider.getCurrentUser();
            List<Cart> allByCreatedBy = cartRepository.findAllByCreatedBy(currentUser);
            if (allByCreatedBy.isEmpty()) {
                throw new NotFoundException("Current user:%s doesn't have any carts".formatted(currentUser));
            }
            cartRepository.deleteAllByCreatedBy(currentUser);
            return ResponseMessage
                    .builder()
                    .message("All carts deleted")
                    .currentUser(currentUser)
                    .entities(allByCreatedBy.stream().map(cartMapper::toDto).toList())
                    .build();
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private boolean checkAvailabilityQuantityBook
            (List<Cart> allByCreatedBy, Book book, Integer expectedCount) {
        if (allByCreatedBy.isEmpty()) {
            return true;
        }
        if (expectedCount < 0) {
            return false;
        }
        return countBooksInCard(allByCreatedBy, book) + expectedCount <= book.getQuantity();
    }

    private Integer countBooksInCard(List<Cart> allByCreatedBy, Book book) {
        List<Cart> cartList = allByCreatedBy.stream()
                .filter(cart -> cart.getBook().getId().equals(book.getId()))
                .collect(Collectors.toList());
        if (cartList.isEmpty()) {
            return 0;
        }
        int reduce = cartList.stream()
                .mapToInt(Cart::getQuantity)
                .reduce(0, Integer::sum);
        return reduce;
    }

    @Transactional
    public void removeFromCart(List<Long> cartList) {
        try {
            cartRepository.deleteAllById(cartList);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
