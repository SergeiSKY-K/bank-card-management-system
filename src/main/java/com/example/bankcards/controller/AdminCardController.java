package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.CreateCardRequestDto;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public class AdminCardController {

    private final CardService cardService;

    @PostMapping
    public CardResponseDto createCard(
            @Valid @RequestBody CreateCardRequestDto request
    ) {
        return cardService.createCard(request);
    }

    @GetMapping
    public Page<CardResponseDto> getAllCards(
            @RequestParam(required = false) CardStatus status,
            Pageable pageable
    ) {
        return cardService.getAllCards(status, pageable);
    }

    @PatchMapping("/{id}/block")
    public CardResponseDto blockCard(@PathVariable Long id) {
        return cardService.blockCard(id);
    }
    @PatchMapping("/{id}/activate")
    public CardResponseDto activateCard(@PathVariable Long id) {
        return cardService.activateCard(id);
    }
    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }
}