package com.demo.fintrack.Domain.Entity;

import com.demo.fintrack.Domain.InvestmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "investments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentType assetType;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal averagePrice;

    @Column(nullable = false)
    private BigDecimal currentValue;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
