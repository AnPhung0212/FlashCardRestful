package com.anpk.firstDemoLearnSpring.dtos.inputs.Deck;

import java.util.UUID;

import lombok.Data;

@Data
public class DeckDeleteRequest {
    private String userKey; // Thêm trường userKey để xác định người dùng
    private UUID id; // Thêm trường id để xác định bộ thẻ cần
}
