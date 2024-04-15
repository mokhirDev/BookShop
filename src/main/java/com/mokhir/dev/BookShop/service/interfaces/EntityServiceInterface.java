package com.mokhir.dev.BookShop.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityServiceInterface<E, RQ, RS, ID>{
    RS getById(ID id);
    Page<RS> findAll(Pageable pageable);
    RS register(RQ rq);
    RS remove(RQ rq);
    RS update(RQ e);
}
