package io.spring.infrastructure.favorite;

import io.spring.core.favorite.ItemFavorite;
import io.spring.core.favorite.ItemFavoriteRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisItemFavoriteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({MyBatisItemFavoriteRepository.class})
public class MyBatisItemFavoriteRepositoryTest extends DbTestBase {
  @Autowired private ItemFavoriteRepository itemFavoriteRepository;

  @Autowired private io.spring.infrastructure.mybatis.mapper.ItemFavoriteMapper itemFavoriteMapper;

  @Test
  public void should_save_and_fetch_itemFavorite_success() {
    ItemFavorite itemFavorite = new ItemFavorite("123", "456");
    itemFavoriteRepository.save(itemFavorite);
    Assertions.assertNotNull(
        itemFavoriteMapper.find(itemFavorite.getItemId(), itemFavorite.getUserId()));
  }

  @Test
  public void should_remove_favorite_success() {
    ItemFavorite itemFavorite = new ItemFavorite("123", "456");
    itemFavoriteRepository.save(itemFavorite);
    itemFavoriteRepository.remove(itemFavorite);
    Assertions.assertFalse(itemFavoriteRepository.find("123", "456").isPresent());
  }
}
