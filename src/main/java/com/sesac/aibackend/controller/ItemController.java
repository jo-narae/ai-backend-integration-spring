package com.sesac.aibackend.controller;

import com.sesac.aibackend.domain.Item;
import com.sesac.aibackend.dto.ItemRequest;
import com.sesac.aibackend.dto.ItemResponse;
import com.sesac.aibackend.error.NotFoundException;
import com.sesac.aibackend.repository.ItemRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Day 3 — Day 2의 인메모리 컨트롤러({@link Day2ItemController})를
 * JPA로 영속화한 동일 패턴입니다.
 *
 * 같은 URL `/items` 를 사용하므로 Day 2에는 `/legacy/items`,
 * Day 3 이후로는 `/items` 를 호출합니다.
 *
 * Day 4 이후 인증된 사용자만 접근 가능 (SecurityConfig anyRequest authenticated).
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

    private final ItemRepository repository;

    @GetMapping
    public List<ItemResponse> list() {
        return repository.findAll().stream().map(ItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("item", id));
        return ItemResponse.from(item);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody ItemRequest req) {
        Item saved = repository.save(req.toEntity());
        URI location = URI.create("/items/" + saved.getId());
        return ResponseEntity.created(location).body(ItemResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @Valid @RequestBody ItemRequest req) {
        Item item = repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("item", id));
        item.setName(req.name());
        item.setPrice(req.price());
        return ItemResponse.from(repository.save(item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw NotFoundException.of("item", id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
