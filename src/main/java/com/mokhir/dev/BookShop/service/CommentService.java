package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.comments.CommentRequest;
import com.mokhir.dev.BookShop.aggregation.dto.comments.CommentResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Book;
import com.mokhir.dev.BookShop.aggregation.entity.Comments;
import com.mokhir.dev.BookShop.aggregation.mapper.CommentMapper;
import com.mokhir.dev.BookShop.exceptions.CurrentUserNotOwnCurrentEntityException;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import com.mokhir.dev.BookShop.exceptions.EntityHaveDuplicateException;
import com.mokhir.dev.BookShop.exceptions.NotFoundException;
import com.mokhir.dev.BookShop.jwt.JwtProvider;
import com.mokhir.dev.BookShop.repository.interfaces.BookRepository;
import com.mokhir.dev.BookShop.repository.interfaces.CommentRepository;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentService
        implements EntityServiceInterface<Comments, CommentRequest, CommentResponse, Long> {
    private final CommentRepository repository;
    private final BookRepository bookRepository;
    private final JwtProvider jwtProvider;
    private final CommentMapper mapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public Page<CommentResponse> findAll(Pageable pageable) {
        Page<Comments> all = repository.findAll(pageable);
        return all.map(comment -> {
            Book book = bookRepository.findById(comment.getBookId()).get();
            CommentResponse commentResponse = mapper.toDto(comment);
            commentResponse.setBookId(book.getId());
            return commentResponse;
        });
    }

    @Override
    public CommentResponse getById(Long id) {
        try {
            if (id == null) {
                throw new NotFoundException("id is null");
            }
            Comments comments = repository.findById(id).orElseThrow(() -> new NotFoundException("id not found"));
            return commentMapper.toDto(comments);
        } catch (NotFoundException e) {
            throw new NotFoundException("id not found");
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }


    @Override
    public CommentResponse register(CommentRequest commentRequest) {
        try {
            Optional<Book> byId = bookRepository.findById(commentRequest.getBookId());
            if (byId.isEmpty()) {
                throw new NotFoundException(
                        "Book with id:%d did not found"
                                .formatted(commentRequest.getBookId()));
            }
            if (existDuplicate(commentRequest)) {
                throw new EntityHaveDuplicateException("Current comment already exists");
            }
            Comments entity = mapper.toEntity(commentRequest);
            repository.save(entity);
            return commentMapper.toDto(entity);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex);
        } catch (Exception ex) {
            throw new DatabaseException("Comment controller: register: " + ex.getMessage());
        }
    }

    @Override
    public CommentResponse remove(CommentRequest request) {
        try {
            String currentUser = jwtProvider.getCurrentUser();
            List<Comments> currentUserComments = repository.findCommentsByCreatedBy(currentUser);
            Optional<Comments> findFirst = currentUserComments
                    .stream()
                    .filter(
                            comment -> comment.getId().equals(request.getId())
                    ).findFirst();
            if (findFirst.isEmpty()) {
                throw new NotFoundException("Did not found comment with id:%d from your comments: "
                        .formatted(request.getId()));
            }
            Comments deletingComment = findFirst.get();
            repository.delete(deletingComment);
            return mapper.toDto(deletingComment);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    @Override
    public CommentResponse update(CommentRequest request) {
        try {
            Comments commentForUpdating = repository.findById(request.getId())
                    .orElseThrow(() ->
                            new NotFoundException("Comment did not found, with id: " + request.getId()));
            String currentUser = jwtProvider.getCurrentUser();
            String createdBy = commentForUpdating.getCreatedBy();
            if (!currentUser.equals(createdBy)) {
                throw new CurrentUserNotOwnCurrentEntityException
                        ("Current user:%s, doesn't own current comment with id:%d"
                                .formatted(currentUser, request.getId()));
            }
            mapper.updateFromDto(request, commentForUpdating);
            Comments save = commentRepository.save(commentForUpdating);
            return mapper.toDto(save);
        } catch (NotFoundException ex) {
            throw new NotFoundException("CommentService: update: " + ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException("CommentService: update: " + ex.getMessage());
        }
    }

    protected boolean existDuplicate(CommentRequest request) {
        String text = request.getText();
        String currentUser = jwtProvider.getCurrentUser();
        List<Comments> commentsByCreatedBy = repository.findCommentsByCreatedBy(currentUser);
        return commentsByCreatedBy
                .stream()
                .anyMatch(comment ->
                        comment.getText().toLowerCase().replaceAll(" ", "")
                                .equals(text.toLowerCase().replaceAll(" ", "")));
    }
}
