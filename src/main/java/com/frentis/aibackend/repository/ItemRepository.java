package com.frentis.aibackend.repository;

import com.frentis.aibackend.domain.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameContaining(String keyword);
}
