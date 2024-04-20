package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.books.BookResponse;
import com.mokhir.dev.BookShop.aggregation.dto.statistics.PopularBookResponse;
import com.mokhir.dev.BookShop.aggregation.dto.users.UserResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final EntityManager em;

    public List<UserResponse> getLastWeekRegisteredUsers() {
        String sql = """
                
                """;
        Query query = em.createNativeQuery(sql, PopularBookResponse.class);
        System.out.println("===>"+query.getResultList());
        return query.getResultList();
    }

    public List<BookResponse> getBook() {
        String sql = """
             
                """;
        Query query = em.createNativeQuery(sql, PopularBookResponse.class);
        System.out.println("===>"+query.getResultList());
        return query.getResultList();
    }

    public List<BookResponse> getAllPopularBooks() {
        String sql = """
                SELECT b.id AS bookId, b.created_by AS author, b.name AS bookName,
                           SUM(o.quantity) AS totalQuantity
                   FROM books b
                            JOIN order_details o ON b.id = o.book_id
                   GROUP BY b.name, b.id
                   ORDER BY SUM(o.quantity) DESC
                   LIMIT 5
                """;
        Query query = em.createNativeQuery(sql, PopularBookResponse.class);
        System.out.println("===>"+query.getResultList());
        return query.getResultList();
    }

    public List<UserResponse> getAllActiveUsers() {
        String sql = """
                
                """;
        Query query = em.createNativeQuery(sql, PopularBookResponse.class);
        System.out.println("===>"+query.getResultList());
        return query.getResultList();
    }

    public List<UserResponse> getAllUsersByPeriod() {
        String sql = """
               
                """;
        Query query = em.createNativeQuery(sql, PopularBookResponse.class);
        System.out.println("===>"+query.getResultList());
        return query.getResultList();
    }
}
