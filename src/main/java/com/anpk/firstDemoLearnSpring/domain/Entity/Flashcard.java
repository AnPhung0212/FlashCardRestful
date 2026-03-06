package com.anpk.firstDemoLearnSpring.domain.Entity;

import com.anpk.firstDemoLearnSpring.domain.common.BaseEntity;
import com.anpk.firstDemoLearnSpring.domain.Enum.ReviewStatus;
import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flashcards")
public class Flashcard extends BaseEntity {

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, length = 1000)
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.NEW;

    private LocalDateTime nextReviewAt;

    private boolean isFavorite;

    // Quan hệ n-n với Tag
    // @JoinTable để định nghĩa bảng trung gian
    // Tương đương với ICollection<Tag> Tags { get; set; } trong EF Core
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "flashcard_tags",
        joinColumns = @JoinColumn(name = "flashcard_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // FetchType.LAZY: Load relations only when accessed (Better for performance)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;
}

