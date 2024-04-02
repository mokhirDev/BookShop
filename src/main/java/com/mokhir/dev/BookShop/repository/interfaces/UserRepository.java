package com.mokhir.dev.BookShop.repository.interfaces;

import com.mokhir.dev.BookShop.aggregation.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
}
