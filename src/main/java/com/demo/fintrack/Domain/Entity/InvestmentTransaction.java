package com.demo.fintrack.Domain.Entity;

import com.demo.fintrack.Domain.InvestmentTransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "investment_transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvestmentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private InvestmentTransactionType type;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "investment_id", nullable = false)
    private Investment investment;

}