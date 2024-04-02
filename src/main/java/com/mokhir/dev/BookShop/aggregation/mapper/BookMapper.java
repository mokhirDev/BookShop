package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookRequest;
import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Books;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper extends EntityMapping<Books, BookRequest, BookResponse> {

}
