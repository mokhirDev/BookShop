package com.mokhir.dev.BookShop.aggregation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users_cart")
@EntityListeners(AuditingEntityListener.class)
public class Cart extends DateAudit implements Serializable {
    @Serial
    private static final long serialVersionUID = 739974703453790820L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book bookId;
    private Integer quantity;
}
