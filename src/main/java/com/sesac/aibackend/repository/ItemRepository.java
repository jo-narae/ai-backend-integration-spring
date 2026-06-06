package com.sesac.aibackend.repository;

import com.sesac.aibackend.domain.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameContaining(String keyword);
}
