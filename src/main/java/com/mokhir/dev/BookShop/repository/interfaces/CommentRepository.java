package com.mokhir.dev.BookShop.repository.interfaces;

import com.mokhir.dev.BookShop.aggregation.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {
    List<Comments> findCommentsByCreatedBy(String createdBy);
}
