package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.EntityMapper;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.service.IncorrectValueException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BookMapper implements EntityMapper<Book, BookRequest, BookResponse> {

    @Override
    @Primary
    public BookResponse toDto(Book entity) {
        if (entity == null) {
            return null;
        }
        return BookResponse.builder()
                .id(entity.getId())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .name(entity.getName())
                .build();
    }

    @Override
    @Primary
    public Book toEntity(BookRequest req) {
        if (req == null) {
            return null;
        }
        return Book.builder()
                .id(req.getId())
                .name(req.getName())
                .price(req.getPrice())
                .quantity(req.getQuantity())
                .build();
    }

    @Override
    @Primary
    public void updateFromDto(BookRequest req, Book entity) {
        try {
            if (req == null) {
                return;
            }
            if (req.getName() != null) {
                entity.setName(req.getName());
            }
            if (req.getQuantity() != null) {
                if (req.getQuantity() > 0) {
                    entity.setQuantity(req.getQuantity());
                } else {
                    throw new IncorrectValueException("Quantity of books must be higher than zero");
                }
            }
            if (req.getPrice() != null) {
                if (req.getPrice() > 0) {
                    entity.setPrice(req.getPrice());
                } else {
                    throw new IncorrectValueException("Quantity of books must be higher than zero");
                }
            } else {
                throw new IncorrectValueException("Price of books must be higher than zero");
            }
            entity.setUpdatedAt(String.valueOf(LocalDateTime.now()));
        } catch (Exception e) {
            throw new DatabaseException("BookMapper: updateFromDto: " + e.getMessage());
        }
    }
}
