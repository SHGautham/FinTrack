package com.demo.fintrack.Domain.DTO.TransactionDTOs;

import com.demo.fintrack.Domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionResponseDTO {

    private UUID id;

    private BigDecimal amount;

    private TransactionType type;

    private LocalDate date;

    private String description;

    private String categoryName;
}
