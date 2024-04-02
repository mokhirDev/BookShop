package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.entity.Users;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService implements EntityServiceInterface<Users, Long> {
    @Override
    public Users getById(Long aLong) {
        return null;
    }

    @Override
    public List<Users> findAll() {
        return null;
    }

    @Override
    public boolean add(Users users) {
        return false;
    }

    @Override
    public boolean remove(Users users) {
        return false;
    }

    @Override
    public boolean removeById(Long aLong) {
        return false;
    }

    @Override
    public Users update(Users users) {
        return null;
    }
}
