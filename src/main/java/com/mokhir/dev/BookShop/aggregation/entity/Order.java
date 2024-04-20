package com.mokhir.dev.BookShop.aggregation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Order extends DateAudit implements Serializable, Comparable<Order> {
    @Serial
    private static final long serialVersionUID = -1281151854203875660L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long totalAmount;
    private Long totalPrice;
    @Column(name = "status", columnDefinition = "boolean default true", nullable = false)
    private boolean status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetails> orderDetails = new ArrayList<>();

    @Override
    public int compareTo(Order o) {
        return this.getCreatedAt().compareTo(o.getCreatedAt());
    }
}
