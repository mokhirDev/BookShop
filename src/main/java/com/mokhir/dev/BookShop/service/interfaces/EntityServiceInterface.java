package com.mokhir.dev.BookShop.service.interfaces;

import java.util.List;

public interface EntityServiceInterface<E, ID>{
    E getById(ID id);
    List<E> findAll();
    boolean add(E e);
    boolean remove(E e);
    boolean removeById(ID id);
    E update(E e);
}
