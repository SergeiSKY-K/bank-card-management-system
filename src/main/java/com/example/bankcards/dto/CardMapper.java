package com.example.bankcards.dto;


import com.example.bankcards.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardResponseDto toDto(Card card) {
        return new CardResponseDto(
                card.getId(),
                maskCardNumber(card.getLastFourDigits()),
                card.getExpirationDate(),
                card.getStatus(),
                card.getBalance(),
                card.getUser().getId(),
                card.getUser().getUsername()
        );
    }

    private String maskCardNumber(String lastFourDigits) {
        return "**** **** **** " + lastFourDigits;
    }
}