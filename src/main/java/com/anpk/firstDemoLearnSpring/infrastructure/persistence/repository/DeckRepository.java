package com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository;

import com.anpk.firstDemoLearnSpring.domain.Entity.Deck;
import com.anpk.firstDemoLearnSpring.domain.Enum.DeckVisibility;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeckRepository extends JpaRepository<Deck, UUID> {
    // Tìm các Deck của một User cụ thể
    // Tương đương với db.Decks.Where(x => x.UserId == userId).ToList()
    List<Deck> findByUserId(UUID userId);
    // Lấy deck của bản thân
    Page<Deck> findAllByUserEmail(String email, Pageable pageable);

    // Lấy deck công khai hoặc filter theo visibility
    Page<Deck> findAllByVisibility(DeckVisibility visibility, Pageable pageable);

}

