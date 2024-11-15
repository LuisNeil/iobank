package com.ltjeda.web.app.iobank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String accountId;
    private Double balance;
    private String accountName;
    @Column(unique = true, nullable = false)
    private Long accountNumber;
    private String currency;
    private String code;
    private String label;
    private Character symbol;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String status;
    private String type;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transaction> transactions;
}
