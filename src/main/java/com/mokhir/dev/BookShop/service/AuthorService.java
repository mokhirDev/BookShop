package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.entity.Authors;
import com.mokhir.dev.BookShop.repository.interfaces.EntityRepositoryInterface;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthorService implements EntityServiceInterface<Authors, Long> {
    private final EntityRepositoryInterface<Authors, Long> repository;

    @Override
    public Authors getById(Long aLong) {
        return null;
    }

    @Override
    public List<Authors> findAll() {
        return null;
    }

    @Override
    public boolean add(Authors authors) {
        return false;
    }

    @Override
    public boolean remove(Authors authors) {
        return false;
    }

    @Override
    public boolean removeById(Long aLong) {
        return false;
    }

    @Override
    public Authors update(Authors authors) {
        return null;
    }
}
