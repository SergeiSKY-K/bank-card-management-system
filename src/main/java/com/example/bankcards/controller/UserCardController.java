package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;

    @GetMapping("/my")
    public Page<CardResponseDto> getMyCards(
            Authentication authentication,
            Pageable pageable
    ) {
        return cardService.getMyCards(authentication.getName(), pageable);
    }

    @PostMapping("/transfer")
    public CardResponseDto transfer(
            Authentication authentication,
            @Valid @RequestBody TransferRequestDto request
    ) {
        return cardService.transfer(authentication.getName(), request);
    }
    @PatchMapping("/{id}/request-block")
    public CardResponseDto requestBlockCard(
            Authentication authentication,
            @PathVariable Long id
    ) {
        return cardService.requestBlockCard(authentication.getName(), id);
    }
}