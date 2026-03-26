package com.anpk.firstDemoLearnSpring.Services.DeckService;

import com.anpk.firstDemoLearnSpring.domain.Entity.Deck;
import com.anpk.firstDemoLearnSpring.domain.Enum.DeckVisibility;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckCreateRequest;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckDeleteRequest;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckDetailRequest;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckUpdateRequest;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Deck.DeckDetailResponse;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Deck.DeckResponse;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Deck.DeckUpdateResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
public interface DeckService {
    DeckResponse createDeck(DeckCreateRequest request, String userKey);
    Page<DeckResponse> getDecks(Pageable pageable, DeckVisibility visibility);
    Page<DeckResponse> getMyDecks(String userKey, Pageable pageable);
    DeckDetailResponse getDeckDetail(DeckDetailRequest request);
    DeckUpdateResponse updateDeck(DeckUpdateRequest request);
    void deleteDeck(DeckDeleteRequest request);
}
