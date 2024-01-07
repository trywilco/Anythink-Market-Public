package io.spring.core.favorite;

import java.util.Optional;

public interface ItemFavoriteRepository {
  void save(ItemFavorite itemFavorite);

  Optional<ItemFavorite> find(String itemId, String userId);

  void remove(ItemFavorite favorite);
}
