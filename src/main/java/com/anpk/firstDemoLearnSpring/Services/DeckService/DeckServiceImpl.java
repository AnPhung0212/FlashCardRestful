package com.anpk.firstDemoLearnSpring.Services.DeckService;

import com.anpk.firstDemoLearnSpring.domain.Enum.DeckVisibility;
import com.anpk.firstDemoLearnSpring.domain.Entity.Deck;
import com.anpk.firstDemoLearnSpring.domain.Entity.User;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckCreateRequest;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckUpdateRequest;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Deck.DeckDetailResponse;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Deck.DeckResponse;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Deck.DeckUpdateResponse;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Flashcard.FlashcardResponse;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.DeckRepository;
import com.anpk.firstDemoLearnSpring.infrastructure.persistence.repository.UserRepository;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckDetailRequest;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckDeleteRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeckServiceImpl implements DeckService {
    private final DeckRepository deckRepository;
    private final UserRepository userRepository; // TODO: Inject UserRepository để lấy thông tin user khi tạo deck

    // tạo Deck mới, cần lấy thông tin user từ userKey để gán vào Deck
    @Override
    public DeckResponse createDeck(DeckCreateRequest request, String userKey) {
        User user = userRepository.findByEmail(userKey)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Deck deck = new Deck();
        deck.setTitle(request.getTitle());
        deck.setDescription(request.getDescription());
        deck.setVisibility(request.getVisibility());
        deck.setUser(user);
        deck = deckRepository.save(deck);

        DeckResponse response = new DeckResponse();
        response.setTitle(deck.getTitle());
        response.setDescription(deck.getDescription());
        response.setVisibility(deck.getVisibility());
        response.setOwnerUsername(user.getUsername());
        response.setFlashcardCount(0);
        response.setId(deck.getId());
        return response;
    }

    // Lấy danh sách Deck theo visibility, có phân trang cho client xem dễ dàng hơn
    @Override
    public Page<DeckResponse> getDecks(Pageable pageable, DeckVisibility visibility) {
        Page<Deck> decks = deckRepository.findAllByVisibility(visibility, pageable);
        return decks.map(deck -> {
            DeckResponse resp = new DeckResponse();
            resp.setTitle(deck.getTitle());
            resp.setDescription(deck.getDescription());
            resp.setVisibility(deck.getVisibility());
            resp.setOwnerUsername(deck.getUser().getUsername());
            resp.setFlashcardCount(deck.getFlashcards().size());
            return resp;
        });
    }

    @Override
    public Page<DeckResponse> getMyDecks(String userKey, Pageable pageable) {
        User user = userRepository.findByEmail(userKey)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Page<Deck> decks = deckRepository.findAllByUserEmail(user.getEmail(), pageable);
        return decks.map(deck -> {
            DeckResponse resp = new DeckResponse();
            resp.setTitle(deck.getTitle());
            resp.setDescription(deck.getDescription());
            resp.setVisibility(deck.getVisibility());
            resp.setOwnerUsername(deck.getUser().getUsername());
            resp.setFlashcardCount(deck.getFlashcards().size());
            resp.setId(deck.getId());
            return resp;
        });
    }

    @Override
    public DeckDetailResponse getDeckDetail(DeckDetailRequest request) {
        UUID id = request.getId();
        String userKey = request.getUserKey();
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));
        // Kiểm tra quyền truy cập: Nếu deck không phải của user.
        if (deck.getVisibility() == DeckVisibility.PRIVATE) {
            if (!deck.getUser().getEmail().equals(userKey)) {
                throw new RuntimeException("Không có quyền truy cập");
            }
        } else if (deck.getVisibility() == DeckVisibility.PROTECTED) {
            boolean isOwner = deck.getUser().getEmail().equals(userKey);
            boolean isShared = deck.getSharedEmails() != null && deck.getSharedEmails().contains(userKey);
            if (!isOwner && !isShared) {
                throw new RuntimeException("Không có quyền truy cập");
            }
        }
        DeckDetailResponse resp = new DeckDetailResponse();
        resp.setTitle(deck.getTitle());
        resp.setDescription(deck.getDescription());
        resp.setVisibility(deck.getVisibility());
        resp.setOwnerUsername(deck.getUser().getUsername());
        // Map flashcards
        List<FlashcardResponse> flashcardResponses = deck.getFlashcards().stream().map(f -> {
            FlashcardResponse fr = new FlashcardResponse();
            fr.setQuestion(f.getQuestion());
            fr.setAnswer(f.getAnswer());
            fr.setStatus(f.getStatus());
            fr.setFavorite(f.isFavorite());
            return fr;
        }).toList();
        resp.setFlashcards(flashcardResponses);
        return resp;
    }

    @Override
    public DeckUpdateResponse updateDeck(DeckUpdateRequest request) {
        UUID id = request.getId();
        String userKey = request.getUserKey();
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));
        // Chỉ owner mới được update
        if (!deck.getUser().getEmail().equals(userKey)) {
            throw new RuntimeException("Không có quyền cập nhật deck này");
        }
        if (request.getTitle() != null) deck.setTitle(request.getTitle());
        if (request.getDescription() != null) deck.setDescription(request.getDescription());
        if (request.getVisibility() != null) deck.setVisibility(request.getVisibility());
        deck = deckRepository.save(deck);

        DeckUpdateResponse resp = new DeckUpdateResponse();
        resp.setTitle(deck.getTitle());
        resp.setDescription(deck.getDescription());
        resp.setVisibility(deck.getVisibility());
        resp.setFlashcardCount(deck.getFlashcards().size());
        return resp;
    }

    @Override
    public void deleteDeck(DeckDeleteRequest request) {
        UUID id = request.getId();
        String userKey = request.getUserKey();
        Deck deck = deckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deck not found"));
        // Chỉ owner mới được xóa
        if (!deck.getUser().getEmail().equals(userKey)) {
            throw new RuntimeException("Không có quyền xóa deck này");
        }
        deckRepository.delete(deck);
    }
}
