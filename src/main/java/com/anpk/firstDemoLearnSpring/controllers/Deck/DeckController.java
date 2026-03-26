package com.anpk.firstDemoLearnSpring.controllers.Deck;

import com.anpk.firstDemoLearnSpring.Services.DeckService.DeckService;
import com.anpk.firstDemoLearnSpring.domain.Enum.DeckVisibility;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckCreateRequest;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckDeleteRequest;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckDetailRequest;
import com.anpk.firstDemoLearnSpring.dtos.inputs.Deck.DeckUpdateRequest;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Deck.DeckDetailResponse;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Deck.DeckResponse;
import com.anpk.firstDemoLearnSpring.dtos.outputs.Deck.DeckUpdateResponse;
import com.anpk.firstDemoLearnSpring.helpers.common.ApiResponse;
// Không cần import SecurityContext của nimbusds

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.anpk.firstDemoLearnSpring.helpers.common.SecurityHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
@Tag(name = "Deck", description = "API quản lý bộ thẻ")
public class DeckController {
    private final DeckService deckService;
    @PostMapping("/create-deck")
    @Operation(summary = "Tạo deck mới", description = "Tạo một deck mới với thông tin được cung cấp")
    public ResponseEntity<ApiResponse<DeckResponse>> createDeck(@Valid @RequestBody DeckCreateRequest request) {
        DeckResponse data = deckService.createDeck(request, SecurityHelper.getCurrentUserEmail());
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Tạo deck thành công", data));
    }

    @GetMapping("/list-decks")
    @Operation(summary = "Lấy danh sách deck", description = "Lấy danh sách deck theo visibility, có phân trang")
    public ResponseEntity<ApiResponse<Page<DeckResponse>>> getDecks(Pageable pageable, @RequestParam DeckVisibility visibility) {
        Page<DeckResponse> data = deckService.getDecks(pageable, visibility);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy danh sách deck thành công", data));
    }

    @GetMapping("/my-decks")
    @Operation(summary = "Lấy deck của tôi", description = "Lấy danh sách deck thuộc về người dùng hiện tại")
    public ResponseEntity<ApiResponse<Page<DeckResponse>>> getMyDecks(Pageable pageable) {
        Page<DeckResponse> data = deckService.getMyDecks(SecurityHelper.getCurrentUserEmail(), pageable);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy deck của tôi thành công", data));
    }

    @GetMapping("/detail-deck")
    @Operation(summary = "Lấy chi tiết deck", description = "Lấy thông tin chi tiết của một deck")
    public ResponseEntity<ApiResponse<DeckDetailResponse>> getDeckDetail(@Valid @RequestBody DeckDetailRequest request) {
        DeckDetailResponse data = deckService.getDeckDetail(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy chi tiết deck thành công", data));
    }

    @PutMapping("/update-deck")
    @Operation(summary = "Cập nhật deck", description = "Cập nhật thông tin của một deck")
    public ResponseEntity<ApiResponse<DeckUpdateResponse>> updateDeck(@Valid @RequestBody DeckUpdateRequest request) {
        DeckUpdateResponse data = deckService.updateDeck(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Cập nhật deck thành công", data));
    }

    @DeleteMapping("/delete-deck")
    @Operation(summary = "Xóa deck", description = "Xóa một deck nếu người dùng có quyền")
    public ResponseEntity<ApiResponse<String>> deleteDeck(@Valid @RequestBody DeckDeleteRequest request) {
        deckService.deleteDeck(request);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Xóa deck thành công", "Xóa thành công"));
    }

}
