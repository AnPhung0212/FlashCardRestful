package com.anpk.firstDemoLearnSpring.dtos.outputs.Deck;

import java.util.UUID;

import com.anpk.firstDemoLearnSpring.domain.Enum.DeckVisibility;

import lombok.Data;
// DTO cơ bản của Deck, chỉ bao gồm thông tin cơ bản mà không có danh sách flashcards
@Data
public class DeckResponse {
    private String title;
    private String description;
    private DeckVisibility visibility;
    private String ownerUsername;
    private int flashcardCount;
    private UUID id;
}
