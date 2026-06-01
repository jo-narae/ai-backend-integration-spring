package com.frentis.aibackend.controller;

import com.frentis.aibackend.domain.Item;
import com.frentis.aibackend.dto.ItemRequest;
import com.frentis.aibackend.dto.ItemResponse;
import com.frentis.aibackend.error.NotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Day 2 B5~B7 — JPA 없이 인메모리로 REST CRUD를 학습합니다.
 *
 * URL: /legacy/items
 * Day 3에서 동일 패턴을 JPA로 전환하면 {@link ItemController}가 됩니다.
 */
@RestController
@RequestMapping("/legacy/items")
public class Day2ItemController {

    private final Map<Long, Item> storage = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @GetMapping
    public List<ItemResponse> list() {
        return storage.values().stream().map(ItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id) {
        Item item = storage.get(id);
        if (item == null) {
            throw NotFoundException.of("item", id);
        }
        return ItemResponse.from(item);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> create(@Valid @RequestBody ItemRequest req) {
        long id = sequence.getAndIncrement();
        Item saved = Item.builder().id(id).name(req.name()).price(req.price()).build();
        storage.put(id, saved);
        return ResponseEntity.created(URI.create("/legacy/items/" + id)).body(ItemResponse.from(saved));
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @Valid @RequestBody ItemRequest req) {
        Item existing = storage.get(id);
        if (existing == null) {
            throw NotFoundException.of("item", id);
        }
        existing.setName(req.name());
        existing.setPrice(req.price());
        return ItemResponse.from(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (storage.remove(id) == null) {
            throw NotFoundException.of("item", id);
        }
        return ResponseEntity.noContent().build();
    }
}
