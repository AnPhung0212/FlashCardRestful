package com.anpk.firstDemoLearnSpring.dtos.outputs.Flashcard;

import java.util.UUID;

import com.anpk.firstDemoLearnSpring.domain.Enum.ReviewStatus;

import lombok.Data;

// DTO cơ bản của Flashcard, chỉ bao gồm thông tin cơ bản mà không có thông tin chi tiết về Deck
@Data
public class FlashcardResponse {
    private String question;
    private String answer;
    private ReviewStatus status;
    private boolean favorite;
    // Nếu cần show id thì thêm vào, mặc định ẩn
    // private UUID id;
}
