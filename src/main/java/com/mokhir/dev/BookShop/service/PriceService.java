package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.entity.Prices;
import com.mokhir.dev.BookShop.service.interfaces.EntityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PriceService implements EntityServiceInterface<Prices, Long> {
    @Override
    public Prices getById(Long aLong) {
        return null;
    }

    @Override
    public List<Prices> findAll() {
        return null;
    }

    @Override
    public boolean add(Prices prices) {
        return false;
    }

    @Override
    public boolean remove(Prices prices) {
        return false;
    }

    @Override
    public boolean removeById(Long aLong) {
        return false;
    }

    @Override
    public Prices update(Prices prices) {
        return null;
    }
}
