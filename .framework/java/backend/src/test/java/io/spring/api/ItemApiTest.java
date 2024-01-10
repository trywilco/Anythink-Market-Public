package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.TestHelper;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.ItemQueryService;
import io.spring.application.data.ItemData;
import io.spring.application.data.ProfileData;
import io.spring.application.item.ItemCommandService;
import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.user.User;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ItemApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class ItemApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private ItemQueryService itemQueryService;

  @MockBean private ItemRepository itemRepository;

  @MockBean ItemCommandService itemCommandService;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_read_item_success() throws Exception {
    String slug = "test-new-item";
    DateTime time = new DateTime();
    Item item =
        new Item(
            "Test New Item",
            "Desc",
            "Image",
            Arrays.asList("java", "spring", "jpg"),
            user.getId(),
            time);
    ItemData itemData = TestHelper.getItemDataFromItemAndUser(item, user);

    when(itemQueryService.findBySlug(eq(slug), eq(null))).thenReturn(Optional.of(itemData));

    RestAssuredMockMvc.when()
        .get("/api/items/{slug}", slug)
        .then()
        .statusCode(200)
        .body("item.slug", equalTo(slug))
        .body("item.image", equalTo(itemData.getImage()))
        .body("item.createdAt", equalTo(ISODateTimeFormat.dateTime().withZoneUTC().print(time)));
  }

  @Test
  public void should_404_if_item_not_found() throws Exception {
    when(itemQueryService.findBySlug(anyString(), any())).thenReturn(Optional.empty());
    RestAssuredMockMvc.when().get("/api/items/not-exists").then().statusCode(404);
  }

  @Test
  public void should_update_item_content_success() throws Exception {
    List<String> tagList = Arrays.asList("java", "spring", "jpg");

    Item originalItem =
        new Item("old title", "old description", "old image", tagList, user.getId());

    Item updatedItem = new Item("new title", "new description", "old image", tagList, user.getId());

    Map<String, Object> updateParam =
        prepareUpdateParam(
            updatedItem.getTitle(), updatedItem.getImage(), updatedItem.getDescription());

    ItemData updatedItemData = TestHelper.getItemDataFromItemAndUser(updatedItem, user);

    when(itemRepository.findBySlug(eq(originalItem.getSlug())))
        .thenReturn(Optional.of(originalItem));
    when(itemCommandService.updateItem(eq(originalItem), any())).thenReturn(updatedItem);
    when(itemQueryService.findBySlug(eq(updatedItem.getSlug()), eq(user)))
        .thenReturn(Optional.of(updatedItemData));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(updateParam)
        .when()
        .put("/api/items/{slug}", originalItem.getSlug())
        .then()
        .statusCode(200)
        .body("item.slug", equalTo(updatedItemData.getSlug()));
  }

  @Test
  public void should_get_403_if_not_user_to_update_item() throws Exception {
    String title = "new-title";
    String image = "new image";
    String description = "new description";
    Map<String, Object> updateParam = prepareUpdateParam(title, image, description);

    User anotherUser = new User("test@test.com", "test", "123123", "", "");

    Item item =
        new Item(
            title, description, image, Arrays.asList("java", "spring", "jpg"), anotherUser.getId());

    DateTime time = new DateTime();
    ItemData itemData =
        new ItemData(
            item.getId(),
            item.getSlug(),
            item.getTitle(),
            item.getDescription(),
            item.getImage(),
            false,
            0,
            time,
            time,
            Arrays.asList("joda"),
            new ProfileData(
                anotherUser.getId(),
                anotherUser.getUsername(),
                anotherUser.getBio(),
                anotherUser.getImage(),
                false));

    when(itemRepository.findBySlug(eq(item.getSlug()))).thenReturn(Optional.of(item));
    when(itemQueryService.findBySlug(eq(item.getSlug()), eq(user)))
        .thenReturn(Optional.of(itemData));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(updateParam)
        .when()
        .put("/api/items/{slug}", item.getSlug())
        .then()
        .statusCode(403);
  }

  @Test
  public void should_delete_item_success() throws Exception {
    String title = "title";
    String image = "image";
    String description = "description";

    Item item =
        new Item(title, description, image, Arrays.asList("java", "spring", "jpg"), user.getId());
    when(itemRepository.findBySlug(eq(item.getSlug()))).thenReturn(Optional.of(item));

    given()
        .header("Authorization", "Token " + token)
        .when()
        .delete("/api/items/{slug}", item.getSlug())
        .then()
        .statusCode(204);

    verify(itemRepository).remove(eq(item));
  }

  @Test
  public void should_403_if_not_author_delete_item() throws Exception {
    String title = "new-title";
    String image = "new image";
    String description = "new description";

    User anotherUser = new User("test@test.com", "test", "123123", "", "");

    Item item =
        new Item(
            title, description, image, Arrays.asList("java", "spring", "jpg"), anotherUser.getId());

    when(itemRepository.findBySlug(eq(item.getSlug()))).thenReturn(Optional.of(item));
    given()
        .header("Authorization", "Token " + token)
        .when()
        .delete("/api/items/{slug}", item.getSlug())
        .then()
        .statusCode(403);
  }

  private HashMap<String, Object> prepareUpdateParam(
      final String title, final String image, final String description) {
    return new HashMap<String, Object>() {
      {
        put(
            "item",
            new HashMap<String, Object>() {
              {
                put("title", title);
                put("image", image);
                put("description", description);
              }
            });
      }
    };
  }
}
