package com.mokhir.dev.BookShop.service;

import com.mokhir.dev.BookShop.aggregation.dto.statistics.ActiveUserStatistic;
import com.mokhir.dev.BookShop.aggregation.dto.statistics.AllPopularBookStatistic;
import com.mokhir.dev.BookShop.aggregation.dto.statistics.PopularBookStatistic;
import com.mokhir.dev.BookShop.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final EntityManager em;
    private static final Logger logger = LoggerFactory.getLogger(StatisticService.class);


    /**
     * Retrieves statistics for popular books.
     *
     * @return A list of PopularBookStatistic objects containing the statistics for each book
     * @throws DatabaseException if there is an error accessing the database
     */
    public List<PopularBookStatistic> getBook() {
        try {
            String sql = """
                               SELECT b.id AS bookId, b.created_by AS author, b.name AS bookName,
                               SUM(o.quantity) AS totalQuantity
                       FROM books b
                                JOIN order_details o ON b.id = o.book_id
                       GROUP BY b.name, b.id
                       ORDER BY SUM(o.quantity) DESC
                       LIMIT 5
                    """;
            Query query = em.createNativeQuery(sql, PopularBookStatistic.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error retrieving statistics for popular books", e);
            throw new DatabaseException("StatisticService: getBook: " + e.getMessage());
        }
    }

    /**
     * Retrieves statistics for all popular books.
     *
     * @return A list of AllPopularBookStatistic objects containing the statistics for each popular book
     * @throws DatabaseException if there is an error accessing the database
     */
    public List<AllPopularBookStatistic> getAllPopularBooks() {
        try {
            String sql = """
                       SELECT
                           b.name AS book_name,
                           sum(od.quantity) AS orders_count,
                           COALESCE(comments_count, 0) AS comments_count
                       FROM 
                           books b
                           JOIN order_details od ON b.id = od.book_id
                           LEFT JOIN (
                               SELECT book_id, COUNT(*) AS comments_count 
                               FROM comments GROUP BY book_id
                           ) AS c ON b.id = c.book_id
                       GROUP BY b.id, b.name, comments_count
                       ORDER BY orders_count DESC
                    """;
            Query query = em.createNativeQuery(sql, AllPopularBookStatistic.class);
            return query.getResultList();
        } catch (Exception ex) {
            logger.error("Error retrieving statistics for all popular books", ex);
            throw new DatabaseException("StatisticService: getAllPopularBooks: " + ex.getMessage());
        }
    }

    /**
     * Retrieves statistics for all active users.
     *
     * @return A list of ActiveUserStatistic objects containing the statistics for each active user
     * @throws DatabaseException if there is an error accessing the database
     */
    public List<ActiveUserStatistic> getAllActiveUsers() {
        try {
            String sql =
                    """
                            WITH orders AS (
                                SELECT
                                    od.created_by,
                                    SUM(od.quantity) AS bookCount,
                                    SUM(od.price * od.quantity) AS totalAmount
                                FROM
                                    order_details od
                                GROUP BY
                                    od.created_by
                            ),
                                 comments AS (
                                     SELECT
                                         c.created_by,
                                         COUNT(*) AS comment_count
                                     FROM
                                         comments c
                                     GROUP BY
                                         c.created_by
                                 )
                            SELECT
                                u.id AS id,
                                u.first_name AS name,
                                COALESCE(orders.bookCount, 0) AS bookCount,
                                COALESCE(orders.totalAmount, 0) AS totalAmount,
                                COALESCE(comments.comment_count, 0) AS commentCount
                            FROM
                                users u
                                    LEFT JOIN
                                orders ON u.username = orders.created_by
                                    LEFT JOIN
                                comments ON u.username = comments.created_by
                            WHERE
                                COALESCE(orders.bookCount, 0) > 1
                              AND COALESCE(comments.comment_count, 0) > 1;
                            """;
            Query query = em.createNativeQuery(sql, ActiveUserStatistic.class);
            return query.getResultList();
        } catch (Exception ex) {
            logger.error("Error retrieving statistics for all active users", ex);
            throw new DatabaseException("StatisticService: getAllActiveUsers: " + ex.getMessage());
        }
    }
}
