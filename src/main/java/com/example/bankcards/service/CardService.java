package com.example.bankcards.service;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.CreateCardRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardEncryptionService cardEncryptionService;
    private final CardMapper cardMapper;

    public CardResponseDto createCard(CreateCardRequestDto request) {

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String encryptedCardNumber =
                cardEncryptionService.encrypt(request.cardNumber());

        String lastFourDigits = request.cardNumber()
                .substring(request.cardNumber().length() - 4);

        Card card = new Card();
        card.setEncryptedCardNumber(encryptedCardNumber);
        card.setLastFourDigits(lastFourDigits);
        card.setExpirationDate(request.expirationDate());
        card.setBalance(request.balance());
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);

        Card savedCard = cardRepository.save(card);

        return cardMapper.toDto(savedCard);
    }

    public Page<CardResponseDto> getAllCards(
            CardStatus status,
            Pageable pageable
    ) {
        Page<Card> cards;

        if (status == null) {
            cards = cardRepository.findAll(pageable);
        } else {
            cards = cardRepository.findByStatus(status, pageable);
        }

        return cards.map(cardMapper::toDto);
    }
    public Page<CardResponseDto> getMyCards(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cardRepository.findByUser(user, pageable)
                .map(cardMapper::toDto);
    }
    public CardResponseDto blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        card.setStatus(CardStatus.BLOCKED);

        Card savedCard = cardRepository.save(card);

        return cardMapper.toDto(savedCard);
    }

    public CardResponseDto activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        card.setStatus(CardStatus.ACTIVE);

        Card savedCard = cardRepository.save(card);

        return cardMapper.toDto(savedCard);
    }
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new RuntimeException("Card not found");
        }

        cardRepository.deleteById(id);
    }

}
