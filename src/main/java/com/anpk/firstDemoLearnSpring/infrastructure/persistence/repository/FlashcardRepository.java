package com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository;

import com.anpk.firstDemoLearnSpring.domain.Entity.Flashcard;
import com.anpk.firstDemoLearnSpring.domain.Enum.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, UUID> {
    // Lấy tất cả Flashcard trong một Deck
    List<Flashcard> findByDeckId(UUID deckId);

    // Tìm các thẻ cần ôn tập (NextReviewAt <= thời gian hiện tại)
    // Tương đương db.Flashcards.Where(x => x.NextReviewAt <= targetTime && x.Status != ReviewStatus.MASTERED)
    List<Flashcard> findByNextReviewAtBeforeAndStatusNot(LocalDateTime targetTime, ReviewStatus status);
}

