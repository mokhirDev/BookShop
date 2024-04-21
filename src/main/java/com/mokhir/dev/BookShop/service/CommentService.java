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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    /**
     * Retrieves a page of comments with associated book information.
     *
     * @param pageable The Pageable object specifying the pagination parameters
     * @return A Page object containing a list of CommentResponse objects with associated book IDs
     * @throws NotFoundException if no comments are found
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public Page<CommentResponse> findAll(Pageable pageable) {
        try {
            // Retrieve a page of comments from the repository
            Page<Comments> all = repository.findAll(pageable);
            // Map each comment to a CommentResponse object with associated book ID
            return all.map(comment -> {
                // Retrieve the book associated with the comment
                Book book = bookRepository.findById(comment.getBookId()).orElseThrow(() ->
                        new NotFoundException("Book not found with ID: " + comment.getBookId()));
                // Map the comment to a CommentResponse object and set the book ID
                CommentResponse commentResponse = mapper.toDto(comment);
                commentResponse.setBookId(book.getId());
                return commentResponse;
            });
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Retrieves a comment by its identifier.
     *
     * @param id The identifier of the comment
     * @return A CommentResponse object containing information about the comment
     * @throws NotFoundException if the comment with the specified identifier is not found
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public CommentResponse getById(Long id) {
        try {
            if (id == null) {
                throw new NotFoundException("ID is null");
            }
            // Retrieve the comment from the repository by its ID
            Comments comments = repository.findById(id).orElseThrow(() ->
                    new NotFoundException("Comment not found with ID: " + id));
            // Map the comment entity to a CommentResponse object and return it
            return commentMapper.toDto(comments);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }



    /**
     * Registers a new comment based on the provided CommentRequest object.
     *
     * @param commentRequest The CommentRequest object containing information about the comment to be registered
     * @return A CommentResponse object containing information about the registered comment
     * @throws NotFoundException if the associated book is not found or if the comment already exists
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public CommentResponse register(CommentRequest commentRequest) {
        try {
            // Check if the associated book exists
            Optional<Book> byId = bookRepository.findById(commentRequest.getBookId());
            if (byId.isEmpty()) {
                throw new NotFoundException("Book with ID " + commentRequest.getBookId() + " not found");
            }
            // Check if the comment already exists
            if (existDuplicate(commentRequest)) {
                throw new EntityHaveDuplicateException("Current comment already exists");
            }
            // Map the CommentRequest object to a Comment entity
            Comments entity = mapper.toEntity(commentRequest);
            // Save the comment entity to the database
            repository.save(entity);
            // Map the saved comment entity to a CommentResponse object and return it
            return commentMapper.toDto(entity);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException("Comment controller: register: " + ex.getMessage());
        }
    }

    /**
     * Removes a comment based on the provided CommentRequest object.
     *
     * @param request The CommentRequest object containing the ID of the comment to be removed
     * @return A CommentResponse object containing information about the removed comment
     * @throws NotFoundException if the comment with the specified ID is not found among the current user's comments
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public CommentResponse remove(CommentRequest request) {
        try {
            // Get the username of the current user from the JWT token
            String currentUser = jwtProvider.getCurrentUser();
            // Find all comments created by the current user
            List<Comments> currentUserComments = repository.findCommentsByCreatedBy(currentUser);
            // Find the comment to be removed among the current user's comments
            Optional<Comments> findFirst = currentUserComments.stream()
                    .filter(comment -> comment.getId().equals(request.getId()))
                    .findFirst();
            // If the comment is not found among the current user's comments, throw NotFoundException
            if (findFirst.isEmpty()) {
                throw new NotFoundException("Did not find comment with ID " + request.getId() +
                        " among your comments");
            }
            // Get the comment to be deleted
            Comments deletingComment = findFirst.get();
            // Delete the comment from the database
            repository.delete(deletingComment);
            // Map the deleted comment to a CommentResponse object and return it
            return mapper.toDto(deletingComment);
        } catch (NotFoundException ex) {
            throw new NotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException(ex.getMessage());
        }
    }

    /**
     * Updates a comment based on the provided CommentRequest object.
     *
     * @param request The CommentRequest object containing the information to update the comment
     * @return A CommentResponse object containing information about the updated comment
     * @throws NotFoundException if the comment with the specified
     * ID is not found or does not belong to the current user
     * @throws DatabaseException if there is an error accessing the database
     */
    @Override
    public CommentResponse update(CommentRequest request) {
        try {
            // Find the comment to be updated by its ID
            Comments commentForUpdating = repository.findById(request.getId())
                    .orElseThrow(() ->
                            new NotFoundException("Comment not found with ID: " + request.getId()));
            // Get the username of the current user from the JWT token
            String currentUser = jwtProvider.getCurrentUser();
            // Get the username of the comment's creator
            String createdBy = commentForUpdating.getCreatedBy();
            // Check if the comment belongs to the current user
            if (!currentUser.equals(createdBy)) {
                throw new CurrentUserNotOwnCurrentEntityException(
                        "Current user " + currentUser + " does not own the comment with ID: " + request.getId());
            }
            // Update the comment entity with the information from the request
            mapper.updateFromDto(request, commentForUpdating);
            // Save the updated comment to the database
            Comments savedComment = commentRepository.save(commentForUpdating);
            // Map the updated comment entity to a CommentResponse object and return it
            return mapper.toDto(savedComment);
        } catch (NotFoundException ex) {
            throw new NotFoundException("CommentService: update: " + ex.getMessage());
        } catch (Exception ex) {
            throw new DatabaseException("CommentService: update: " + ex.getMessage());
        }
    }

    protected boolean existDuplicate(CommentRequest request) {
        try {
            // Get the text of the new comment
            String text = request.getText();
            // Get the username of the current user from the JWT token
            String currentUser = jwtProvider.getCurrentUser();
            // Find all comments created by the current user
            List<Comments> commentsByCreatedBy = repository.findCommentsByCreatedBy(currentUser);
            // Check if any of the existing comments have the same text as the new comment
            return commentsByCreatedBy.stream()
                    .anyMatch(comment -> comment.getText().toLowerCase().replaceAll(" ", "")
                            .equals(text.toLowerCase().replaceAll(" ", "")));
        } catch (Exception ex) {
            // Log any exceptions that occur
            logger.error("Error checking for duplicate comments: {}", ex.getMessage());
            // Throw a DatabaseException to indicate a problem accessing the database
            throw new DatabaseException("Error checking for duplicate comments: " + ex.getMessage());
        }
    }

}
