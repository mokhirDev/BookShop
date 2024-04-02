package com.mokhir.dev.BookShop.aggregation.mapper;

import com.mokhir.dev.BookShop.aggregation.dto.users.UserRequest;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import com.mokhir.dev.BookShop.aggregation.entity.Users;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapping<Users, UserRequest, UserResponse> {

}
