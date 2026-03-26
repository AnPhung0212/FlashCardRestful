package com.anpk.firstDemoLearnSpring.dtos.inputs.Deck;

import com.anpk.firstDemoLearnSpring.domain.Enum.DeckVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeckCreateRequest {
     @NotBlank
     @Size(min = 3, max = 100)
    private String title;
     @Size(max = 500)
    private String description;
     @NotNull
    private DeckVisibility visibility;
}
