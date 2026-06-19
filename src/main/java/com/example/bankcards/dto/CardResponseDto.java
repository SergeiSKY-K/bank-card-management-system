package com.example.bankcards.dto;

import com.example.bankcards.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponseDto(
        Long id,
        String maskedCardNumber,
        LocalDate expirationDate,
        CardStatus status,
        BigDecimal balance,
        Long userId,
        String username
) {
}

