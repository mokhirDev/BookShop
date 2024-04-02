package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.price.PriceRequest;
import com.mokhir.dev.BookShop.aggregation.dto.price.PriceResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Prices;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceMapper extends EntityMapping<Prices, PriceRequest, PriceResponse> {

}
