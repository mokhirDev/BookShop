package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.entity.Books;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookService implements EntityServiceInterface<Books, Long> {
    @Override
    public Books getById(Long aLong) {
        return null;
    }

    @Override
    public List<Books> findAll() {
        return null;
    }

    @Override
    public boolean add(Books books) {
        return false;
    }

    @Override
    public boolean remove(Books books) {
        return false;
    }

    @Override
    public boolean removeById(Long aLong) {
        return false;
    }

    @Override
    public Books update(Books books) {
        return null;
    }
}
