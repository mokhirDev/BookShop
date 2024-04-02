package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.authors.AuthorRequest;
import com.mokhir.dev.BookShop.aggregation.dto.authors.AuthorResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Authors;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper extends EntityMapping<Authors, AuthorRequest, AuthorResponse> {

}
