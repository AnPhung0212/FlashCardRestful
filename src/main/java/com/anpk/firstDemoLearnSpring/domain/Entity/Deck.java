package com.anpk.firstDemoLearnSpring.domain.Entity;

import com.anpk.firstDemoLearnSpring.domain.common.BaseEntity;
import com.anpk.firstDemoLearnSpring.domain.Enum.DeckVisibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "decks")
public class Deck extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String description;

    // Quan hệ n-1 với User: Nhiều Decks thuộc về một User
    // FetchType.LAZY: Chỉ load User khi cần thiết
    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Tương đương với ICollection<Flashcard> trong EF Core
    // cascade = CascadeType.ALL: Khi xóa Deck, xóa luôn Flashcard
    // orphanRemoval = true: Khi remove Flashcard khỏi list này, xóa luôn khỏi DB
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flashcard> flashcards = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeckVisibility visibility = DeckVisibility.PRIVATE;

    // Danh sách email user được share (PROTECTED)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "deck_shared_emails", joinColumns = @JoinColumn(name = "deck_id"))
    @Column(name = "email_shared")
    private List<String> sharedEmails = new ArrayList<>();

}

