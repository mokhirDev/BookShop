package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Books;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.EntityMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class BookMapper implements EntityMapper<Books, BookRequest, BookResponse> {

    @Override
    @Primary
    public BookResponse toDto(Books entity) {
        if (entity == null) {
            return null;
        }
        return BookResponse.builder()
                .book(entity)
                .build();
    }

    @Override
    @Primary
    public Books toEntity(BookRequest req) {
        if (req == null) {
            return null;
        }
        return Books.builder()
                .id(req.getId())
                .name(req.getName())
                .price(req.getPrice())
                .quantity(req.getQuantity())
                .build();
    }

    @Override
    @Primary
    public void updateFromDto(BookRequest req, Books entity) {
        if (req == null) {
            return;
        }
        if (req.getCreatedBy() != null) {
            entity.setCreatedBy(req.getCreatedBy());
        }
        if (req.getCreatedAt() != null) {
            entity.setCreatedAt(req.getCreatedAt());
        }
        if (req.getUpdatedAt() != null) {
            entity.setUpdatedAt(req.getUpdatedAt());
        }
        if (req.getName() != null) {
            entity.setName(req.getName());
        }
        if (req.getPrice() != null) {
            entity.setPrice(req.getPrice());
        }
        if (req.getQuantity() != null) {
            entity.setQuantity(req.getQuantity());
        }
    }
}
