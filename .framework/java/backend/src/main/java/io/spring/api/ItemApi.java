package io.spring.api;

import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ItemQueryService;
import io.spring.application.data.ItemData;
import io.spring.application.item.ItemCommandService;
import io.spring.application.item.UpdateItemParam;
import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/items/{slug}")
@AllArgsConstructor
public class ItemApi {
  private ItemQueryService itemQueryService;
  private ItemRepository itemRepository;
  private ItemCommandService itemCommandService;

  @GetMapping
  public ResponseEntity<?> item(
      @PathVariable("slug") String slug, @AuthenticationPrincipal User user) {
    return itemQueryService
        .findBySlug(slug, user)
        .map(itemData -> ResponseEntity.ok(itemResponse(itemData)))
        .orElseThrow(ResourceNotFoundException::new);
  }

  @PutMapping
  public ResponseEntity<?> updateItem(
      @PathVariable("slug") String slug,
      @AuthenticationPrincipal User user,
      @Valid @RequestBody UpdateItemParam updateItemParam) {
    return itemRepository
        .findBySlug(slug)
        .map(
            item -> {
              if (!AuthorizationService.canWriteItem(user, item)) {
                throw new NoAuthorizationException();
              }
              Item updatedItem = itemCommandService.updateItem(item, updateItemParam);
              return ResponseEntity.ok(
                  itemResponse(itemQueryService.findBySlug(updatedItem.getSlug(), user).get()));
            })
        .orElseThrow(ResourceNotFoundException::new);
  }

  @DeleteMapping
  public ResponseEntity deleteItem(
      @PathVariable("slug") String slug, @AuthenticationPrincipal User user) {
    return itemRepository
        .findBySlug(slug)
        .map(
            item -> {
              if (!AuthorizationService.canWriteItem(user, item)) {
                throw new NoAuthorizationException();
              }
              itemRepository.remove(item);
              return ResponseEntity.noContent().build();
            })
        .orElseThrow(ResourceNotFoundException::new);
  }

  private Map<String, Object> itemResponse(ItemData itemData) {
    return new HashMap<String, Object>() {
      {
        put("item", itemData);
      }
    };
  }
}
