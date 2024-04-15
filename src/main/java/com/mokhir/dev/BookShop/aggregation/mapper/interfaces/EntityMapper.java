package com.mokhir.dev.BookShop.aggregation.mapper.interfaces;

public interface EntityMapper <E, RQ, RS>{
    RS toDto(E e);
    E toEntity(RQ rq);
    void updateFromDto(RQ rq, E e);
}
