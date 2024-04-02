package com.mokhir.dev.BookShop.repository.interfaces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@NoRepositoryBean
public interface BaseRepository<E, ID> extends JpaRepository<E, ID> {
}
