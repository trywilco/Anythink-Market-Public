package io.spring.api;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ItemQueryService;
import io.spring.application.data.ItemData;
import io.spring.core.favorite.ItemFavorite;
import io.spring.core.favorite.ItemFavoriteRepository;
import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.user.User;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/items/{slug}/favorite")
@AllArgsConstructor
public class ItemFavoriteApi {
  private ItemFavoriteRepository itemFavoriteRepository;
  private ItemRepository itemRepository;
  private ItemQueryService itemQueryService;

  @PostMapping
  public ResponseEntity favoriteItem(
      @PathVariable("slug") String slug, @AuthenticationPrincipal User user) {
    Item item = itemRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    ItemFavorite itemFavorite = new ItemFavorite(item.getId(), user.getId());
    itemFavoriteRepository.save(itemFavorite);
    return responseItemData(itemQueryService.findBySlug(slug, user).get());
  }

  @DeleteMapping
  public ResponseEntity unfavoriteItem(
      @PathVariable("slug") String slug, @AuthenticationPrincipal User user) {
    Item item = itemRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    itemFavoriteRepository
        .find(item.getId(), user.getId())
        .ifPresent(
            favorite -> {
              itemFavoriteRepository.remove(favorite);
            });
    return responseItemData(itemQueryService.findBySlug(slug, user).get());
  }

  private ResponseEntity<HashMap<String, Object>> responseItemData(final ItemData itemData) {
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("item", itemData);
          }
        });
  }
}
