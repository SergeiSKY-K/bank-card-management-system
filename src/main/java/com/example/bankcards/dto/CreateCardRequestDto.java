package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCardRequestDto(

        @NotBlank
        @Pattern(regexp = "\\d{16}", message = "Card number must contain exactly 16 digits")
        String cardNumber,

        @NotNull
        LocalDate expirationDate,

        @NotNull
        Long userId,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal balance
) {
}