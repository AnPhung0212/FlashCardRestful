package com.anpk.firstDemoLearnSpring.domain.Entity;

import com.anpk.firstDemoLearnSpring.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tags")
public class Tag extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // Quan hệ n-n với Flashcard
    // mappedBy = "tags": Phía Flashcard sẽ định nghĩa @JoinTable
    // Tương đương với ICollection<Flashcard> Flashcards { get; set; } trong EF Core
    @ManyToMany(mappedBy = "tags")
    private Set<Flashcard> flashcards = new HashSet<>();
}

