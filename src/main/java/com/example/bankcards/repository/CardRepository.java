package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card,Long> {
    Page<Card> findByUser(User user, Pageable pageable);
    Page<Card> findByStatus(CardStatus status, Pageable pageable);
    Page<Card> findByUserAndStatus(
            User user,
            CardStatus status,
            Pageable pageable
    );
}
