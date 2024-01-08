package io.spring.api;

import io.spring.application.ItemQueryService;
import io.spring.application.Page;
import io.spring.application.item.ItemCommandService;
import io.spring.application.item.NewItemParam;
import io.spring.core.item.Item;
import io.spring.core.user.User;
import java.util.HashMap;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/items")
@AllArgsConstructor
public class ItemsApi {
  private ItemCommandService itemCommandService;
  private ItemQueryService itemQueryService;

  @PostMapping
  public ResponseEntity createItem(
      @Valid @RequestBody NewItemParam newItemParam, @AuthenticationPrincipal User user) {
    Item item = itemCommandService.createItem(newItemParam, user);
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("item", itemQueryService.findById(item.getId(), user).get());
          }
        });
  }

  @GetMapping(path = "feed")
  public ResponseEntity getFeed(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(itemQueryService.findUserFeed(user, new Page(offset, limit)));
  }

  @GetMapping
  public ResponseEntity getItems(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "20") int limit,
      @RequestParam(value = "tag", required = false) String tag,
      @RequestParam(value = "favorited", required = false) String favoritedBy,
      @RequestParam(value = "seller", required = false) String seller,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(
        itemQueryService.findRecentItems(
            tag, seller, favoritedBy, new Page(offset, limit), user));
  }
}
