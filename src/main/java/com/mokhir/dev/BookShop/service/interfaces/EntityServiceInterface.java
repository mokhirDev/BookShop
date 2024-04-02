package com.mokhir.dev.BookShop.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EntityServiceInterface<E, RQ, RS, ID>{
    RS getById(ID id);
    Page<RS> findAll(Pageable pageable);
    RS add(RQ rq);
    RS remove(RQ rq);
    RS removeById(ID id);
    RS update(RQ e);
}
