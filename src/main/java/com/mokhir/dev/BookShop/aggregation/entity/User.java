package com.mokhir.dev.BookShop.aggregation.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @NotNull
    @Column(unique = true, nullable = false, updatable = false)
    public String username;
    @NotNull
    @Size(max = 100, min = 5)
    private String password;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Role role;
    @Column(name = "is_active", columnDefinition = "boolean default false", nullable = false, insertable = false)
    private Boolean isActive;
    @Column(name = "is_deleted", columnDefinition = "boolean default false", nullable = false, insertable = false)
    private Boolean isDeleted;
    @Column(name = "refresh_token", unique = true, length = 1000)
    private String refreshToken;
}
