package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.dto.comments.CommentRequest;
import com.mokhir.dev.BookShop.aggregation.dto.comments.CommentResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.entity.Comments;
import com.mokhir.dev.BookShop.aggregation.mapper.interfaces.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper implements EntityMapper<Comments, CommentRequest, CommentResponse> {

    @Override
    @Primary
    public CommentResponse toDto(Comments entity) {
        if (entity == null) {
            return null;
        }
        return CommentResponse.builder()
                .id(entity.getId())
                .text(entity.getText())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .bookId(entity.getBookId())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    @Primary
    public Comments toEntity(CommentRequest req) {
        if (req == null) {
            return null;
        }
        return Comments.builder()
                .id(req.getId())
                .text(req.getText())
                .bookId(req.getBookId())
                .build();
    }

    @Override
    @Primary
    public void updateFromDto(CommentRequest req, Comments entity) {
        if (req == null) {
            return;
        }
        if (req.getText() != null) {
            entity.setText(req.getText());
        }
    }
}
