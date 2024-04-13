package com.mokhir.dev.BookShop.aggregation.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;


@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt", "createdBy"},
        allowGetters = true
)
public abstract class DateAudit implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @CreatedDate
    @Column(updatable = false)
    @CurrentTimestamp
    private Instant createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
}
