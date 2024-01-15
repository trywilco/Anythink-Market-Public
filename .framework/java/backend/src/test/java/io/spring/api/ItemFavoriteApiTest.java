package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.ItemQueryService;
import io.spring.application.data.ItemData;
import io.spring.application.data.ProfileData;
import io.spring.core.favorite.ItemFavorite;
import io.spring.core.favorite.ItemFavoriteRepository;
import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.item.Tag;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ItemFavoriteApi.class)
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class ItemFavoriteApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private ItemFavoriteRepository itemFavoriteRepository;

  @MockBean private ItemRepository itemRepository;

  @MockBean private ItemQueryService itemQueryService;

  private Item item;

  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
    User anotherUser = new User("other@test.com", "other", "123", "", "");
    item = new Item("title", "desc", "image", Arrays.asList("java"), anotherUser.getId());
    when(itemRepository.findBySlug(eq(item.getSlug()))).thenReturn(Optional.of(item));
    ItemData itemData =
        new ItemData(
            item.getId(),
            item.getSlug(),
            item.getTitle(),
            item.getDescription(),
            item.getImage(),
            true,
            1,
            item.getCreatedAt(),
            item.getUpdatedAt(),
            item.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
            new ProfileData(
                anotherUser.getId(),
                anotherUser.getUsername(),
                anotherUser.getBio(),
                anotherUser.getImage(),
                false));
    when(itemQueryService.findBySlug(eq(itemData.getSlug()), eq(user)))
        .thenReturn(Optional.of(itemData));
  }

  @Test
  public void should_favorite_an_item_success() throws Exception {
    given()
        .header("Authorization", "Token " + token)
        .when()
        .post("/api/items/{slug}/favorite", item.getSlug())
        .prettyPeek()
        .then()
        .statusCode(200)
        .body("item.id", equalTo(item.getId()));

    verify(itemFavoriteRepository).save(any());
  }

  @Test
  public void should_unfavorite_an_item_success() throws Exception {
    when(itemFavoriteRepository.find(eq(item.getId()), eq(user.getId())))
        .thenReturn(Optional.of(new ItemFavorite(item.getId(), user.getId())));
    given()
        .header("Authorization", "Token " + token)
        .when()
        .delete("/api/items/{slug}/favorite", item.getSlug())
        .prettyPeek()
        .then()
        .statusCode(200)
        .body("item.id", equalTo(item.getId()));
    verify(itemFavoriteRepository).remove(new ItemFavorite(item.getId(), user.getId()));
  }
}
