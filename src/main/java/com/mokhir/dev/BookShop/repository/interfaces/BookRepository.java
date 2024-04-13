package com.mokhir.dev.BookShop.repository.interfaces;

import com.mokhir.dev.BookShop.aggregation.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Books, Long> {
    Optional<Books> findBooksByCreatedBy(String authorUsername);
}
