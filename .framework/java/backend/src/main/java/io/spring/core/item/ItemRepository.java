package io.spring.core.item;

import java.util.Optional;

public interface ItemRepository {

  void save(Item item);

  Optional<Item> findById(String id);

  Optional<Item> findBySlug(String slug);

  void remove(Item item);
}
