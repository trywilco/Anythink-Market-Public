package io.spring.infrastructure.repository;

import io.spring.core.favorite.ItemFavorite;
import io.spring.core.favorite.ItemFavoriteRepository;
import io.spring.infrastructure.mybatis.mapper.ItemFavoriteMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisItemFavoriteRepository implements ItemFavoriteRepository {
  private ItemFavoriteMapper mapper;

  @Autowired
  public MyBatisItemFavoriteRepository(ItemFavoriteMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void save(ItemFavorite itemFavorite) {
    if (mapper.find(itemFavorite.getItemId(), itemFavorite.getUserId()) == null) {
      mapper.insert(itemFavorite);
    }
  }

  @Override
  public Optional<ItemFavorite> find(String itemId, String userId) {
    return Optional.ofNullable(mapper.find(itemId, userId));
  }

  @Override
  public void remove(ItemFavorite favorite) {
    mapper.delete(favorite);
  }
}
