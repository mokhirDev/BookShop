package com.mokhir.dev.BookShop.repository.interfaces;

import com.mokhir.dev.BookShop.aggregation.entity.Books;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Books, Long> {
    Optional<Books> findBooksByCreatedBy(String authorUsername);
    List<Books> findAllBooksByCreatedBy(String authorUsername);
    Page<Books> findAllBooksByCreatedBy(String authorUsername, Pageable pageable);
}
