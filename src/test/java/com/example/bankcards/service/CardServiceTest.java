package com.example.bankcards.service;

import com.example.bankcards.dto.CardMapper;
import com.example.bankcards.dto.CardResponseDto;
import com.example.bankcards.dto.CreateCardRequestDto;
import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.exceptions.InsufficientFundsException;
import com.example.bankcards.exceptions.SameCardTransferException;

import com.example.bankcards.exceptions.CardBlockedException;
import com.example.bankcards.exceptions.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardEncryptionService cardEncryptionService;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    @Test
    void createCard_shouldCreateActiveCard() {
        CreateCardRequestDto request = new CreateCardRequestDto(
                "1234567812345678",
                LocalDate.of(2030, 12, 31),
                1L,
                BigDecimal.valueOf(1000)
        );

        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Card savedCard = new Card();
        savedCard.setId(1L);
        savedCard.setStatus(CardStatus.ACTIVE);
        savedCard.setUser(user);

        CardResponseDto responseDto = new CardResponseDto(
                1L,
                "**** **** **** 5678",
                LocalDate.of(2030, 12, 31),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000),
                1L,
                "user1"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardEncryptionService.encrypt(anyString())).thenReturn("encrypted");
        when(cardRepository.save(any(Card.class))).thenReturn(savedCard);
        when(cardMapper.toDto(savedCard)).thenReturn(responseDto);

        CardResponseDto result = cardService.createCard(request);

        assertEquals(CardStatus.ACTIVE, result.status());
        assertEquals("**** **** **** 5678", result.maskedCardNumber());

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void blockCard_shouldChangeStatusToBlocked() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);

        Card savedCard = new Card();
        savedCard.setId(1L);
        savedCard.setStatus(CardStatus.BLOCKED);
        savedCard.setUser(user);

        CardResponseDto responseDto = new CardResponseDto(
                1L,
                "**** **** **** 5678",
                LocalDate.of(2030, 12, 31),
                CardStatus.BLOCKED,
                BigDecimal.valueOf(1000),
                1L,
                "user1"
        );

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(savedCard);
        when(cardMapper.toDto(savedCard)).thenReturn(responseDto);

        CardResponseDto result = cardService.blockCard(1L);

        assertEquals(CardStatus.BLOCKED, result.status());

        verify(cardRepository).save(card);
    }

    @Test
    void activateCard_shouldChangeStatusToActive() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.BLOCKED);
        card.setUser(user);

        Card savedCard = new Card();
        savedCard.setId(1L);
        savedCard.setStatus(CardStatus.ACTIVE);
        savedCard.setUser(user);

        CardResponseDto responseDto = new CardResponseDto(
                1L,
                "**** **** **** 5678",
                LocalDate.of(2030, 12, 31),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000),
                1L,
                "user1"
        );

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(savedCard);
        when(cardMapper.toDto(savedCard)).thenReturn(responseDto);

        CardResponseDto result = cardService.activateCard(1L);

        assertEquals(CardStatus.ACTIVE, result.status());

        verify(cardRepository).save(card);
    }
    @Test
    void getMyCards_shouldReturnOnlyUserCards() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);

        CardResponseDto responseDto = new CardResponseDto(
                1L,
                "**** **** **** 5678",
                LocalDate.of(2030, 12, 31),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(1000),
                1L,
                "user1"
        );

        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findByUser(user, pageable)).thenReturn(new PageImpl<>(List.of(card)));
        when(cardMapper.toDto(card)).thenReturn(responseDto);

        Page<CardResponseDto> result = cardService.getMyCards("user1", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("**** **** **** 5678", result.getContent().get(0).maskedCardNumber());

        verify(cardRepository).findByUser(user, pageable);
    }

    @Test
    void transfer_shouldTransferMoneySuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(user);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpirationDate(LocalDate.now().plusYears(1));
        fromCard.setBalance(BigDecimal.valueOf(1000));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(user);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpirationDate(LocalDate.now().plusYears(1));
        toCard.setBalance(BigDecimal.valueOf(500));

        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(300));

        CardResponseDto responseDto = new CardResponseDto(
                2L,
                "**** **** **** 2222",
                LocalDate.now().plusYears(1),
                CardStatus.ACTIVE,
                BigDecimal.valueOf(800),
                1L,
                "user1"
        );

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any(Card.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cardMapper.toDto(toCard)).thenReturn(responseDto);

        CardResponseDto result = cardService.transfer("user1", request);

        assertEquals(BigDecimal.valueOf(700), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(800), toCard.getBalance());
        assertEquals(BigDecimal.valueOf(800), result.balance());

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
    }

    @Test
    void transfer_shouldThrowInsufficientFundsException() {
        User user = new User();
        user.setId(1L);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(user);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpirationDate(LocalDate.now().plusYears(1));
        fromCard.setBalance(BigDecimal.valueOf(100));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(user);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpirationDate(LocalDate.now().plusYears(1));
        toCard.setBalance(BigDecimal.valueOf(500));

        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(300));

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(
                InsufficientFundsException.class,
                () -> cardService.transfer("user1", request)
        );
    }
    @Test
    void transfer_shouldThrowSameCardTransferException() {
        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setId(1L);
        card.setUser(user);

        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(1L);
        request.setAmount(BigDecimal.valueOf(100));

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(
                SameCardTransferException.class,
                () -> cardService.transfer("user1", request)
        );
    }
    @Test
    void transfer_shouldThrowCardBlockedException() {
        User user = new User();
        user.setId(1L);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(user);
        fromCard.setStatus(CardStatus.BLOCKED);
        fromCard.setExpirationDate(LocalDate.now().plusYears(1));
        fromCard.setBalance(BigDecimal.valueOf(1000));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(user);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpirationDate(LocalDate.now().plusYears(1));
        toCard.setBalance(BigDecimal.valueOf(500));

        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(100));

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(fromCard));

        when(cardRepository.findById(2L))
                .thenReturn(Optional.of(toCard));

        assertThrows(
                CardBlockedException.class,
                () -> cardService.transfer("user1", request)
        );
    }

    @Test
    void transfer_shouldThrowAccessDeniedException() {
        User currentUser = new User();
        currentUser.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setUser(currentUser);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpirationDate(LocalDate.now().plusYears(1));
        fromCard.setBalance(BigDecimal.valueOf(1000));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setUser(anotherUser);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpirationDate(LocalDate.now().plusYears(1));
        toCard.setBalance(BigDecimal.valueOf(500));

        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(100));

        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(currentUser));

        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(fromCard));

        when(cardRepository.findById(2L))
                .thenReturn(Optional.of(toCard));

        assertThrows(
                AccessDeniedException.class,
                () -> cardService.transfer("user1", request)
        );
    }

}