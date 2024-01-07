package io.spring.core.favorite;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class ItemFavorite {
  private String itemId;
  private String userId;

  public ItemFavorite(String itemId, String userId) {
    this.itemId = itemId;
    this.userId = userId;
  }
}
