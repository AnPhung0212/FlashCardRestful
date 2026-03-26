package com.anpk.firstDemoLearnSpring.dtos.outputs.Deck;

import java.util.List;
import java.util.UUID;

import com.anpk.firstDemoLearnSpring.domain.Enum.DeckVisibility;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Flashcard.FlashcardResponse;

import lombok.Data;

// DTO chi tiết của Deck, bao gồm thông tin cơ bản và danh sách flashcards
@Data
public class DeckDetailResponse {
   //rivate UUID id;
    private String title;
    private String description;
    private DeckVisibility visibility;
    private String ownerUsername;
    private List<FlashcardResponse> flashcards;
}
