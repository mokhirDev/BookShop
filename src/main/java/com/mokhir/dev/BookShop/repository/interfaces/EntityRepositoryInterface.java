package com.mokhir.dev.BookShop.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityRepositoryInterface<E, ID> extends JpaRepository<E, ID> {
    E getById(ID id);
    List<E> findAll();
    boolean add(E e);
    boolean remove(E e);
    boolean removeById(ID id);
    E update(E e);
}
