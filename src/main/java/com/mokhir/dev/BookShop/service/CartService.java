package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.ResponseMessage;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartRequest;
import com.mokhir.dev.BookShop.aggregation.dto.cart.CartResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.entity.Cart;
import com.mokhir.dev.BookShop.aggregation.mapper.CartMapper;
import com.mokhir.dev.BookShop.exceptions.CurrentUserNotOwnCurrentEntityException;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.BookRepository;
import com.mokhir.dev.BookShop.repository.interfaces.CartRepository;
import com.mokhir.dev.BookShop.service.interfaces.CartServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @Override
    public CartResponse addToCart(CartRequest cartRequest) {
        try {
            Long bookId = cartRequest.getBookId();
            Optional<Book> byId = bookRepository.findById(bookId);
            if (byId.isEmpty()) {
                throw new NotFoundException("Book with current id:%d not found".formatted(bookId));
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
            Page<Cart> allByCreatedBy = cartRepository.findAllByCreatedBy(currentUser, pageable);
            if (allByCreatedBy.isEmpty()) {
                throw new NotFoundException("Current user:%s doesn't have any cards".formatted(currentUser));
            }
            return allByCreatedBy.map(cartMapper::toDto);
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
}
