package com.mokhir.dev.BookShop.aggregation.mapper.interfaces;

public interface BaseMapper<E, RS> {
    RS toDto(E e);
}
