package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
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
import io.spring.application.item.ItemCommandService;
import io.spring.core.item.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({ItemsApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class ItemsApiTest extends TestWithCurrentUser {
  @Autowired private MockMvc mvc;

  @MockBean private ItemQueryService itemQueryService;

  @MockBean private ItemCommandService itemCommandService;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_create_item_success() throws Exception {
    String title = "How to train your dragon";
    String slug = "how-to-train-your-dragon";
    String description = "Ever wonder how?";
    String image = "Another image";
    List<String> tagList = asList("reactjs", "angularjs", "dragons");
    Map<String, Object> param = prepareParam(title, description, image, tagList);

    ItemData itemData =
        new ItemData(
            "123",
            slug,
            title,
            description,
            image,
            false,
            0,
            new DateTime(),
            new DateTime(),
            tagList,
            new ProfileData("userid", user.getUsername(), user.getBio(), user.getImage(), false));

    when(itemCommandService.createItem(any(), any()))
        .thenReturn(new Item(title, description, image, tagList, user.getId()));

    when(itemQueryService.findBySlug(eq(Item.toSlug(title)), any())).thenReturn(Optional.empty());

    when(itemQueryService.findById(any(), any())).thenReturn(Optional.of(itemData));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/api/items")
        .then()
        .statusCode(200)
        .body("item.title", equalTo(title))
        .body("item.favorited", equalTo(false))
        .body("item.favoritesCount", equalTo(0))
        .body("item.seller.username", equalTo(user.getUsername()))
        .body("item.seller.id", equalTo(null));

    verify(itemCommandService).createItem(any(), any());
  }

  @Test
  public void should_get_error_message_with_wrong_parameter() throws Exception {
    String title = "";
    String description = "Ever wonder how?";
    String image = "Image URL";
    String[] tagList = {"reactjs", "angularjs", "dragons"};
    Map<String, Object> param = prepareParam(title, description, image, asList(tagList));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/api/items")
        .prettyPeek()
        .then()
        .statusCode(422)
        .body("errors.title[0]", equalTo("can't be empty"));
  }

  @Test
  public void should_get_error_message_with_duplicated_title() {
    String title = "How to train your dragon";
    String slug = "how-to-train-your-dragon";
    String description = "Ever wonder how?";
    String image = "Image URL";
    String[] tagList = {"reactjs", "angularjs", "dragons"};
    Map<String, Object> param = prepareParam(title, description, image, asList(tagList));

    ItemData itemData =
        new ItemData(
            "123",
            slug,
            title,
            description,
            image,
            false,
            0,
            new DateTime(),
            new DateTime(),
            asList(tagList),
            new ProfileData("userid", user.getUsername(), user.getBio(), user.getImage(), false));

    when(itemQueryService.findBySlug(eq(Item.toSlug(title)), any()))
        .thenReturn(Optional.of(itemData));

    when(itemQueryService.findById(any(), any())).thenReturn(Optional.of(itemData));

    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body(param)
        .when()
        .post("/api/items")
        .prettyPeek()
        .then()
        .statusCode(422);
  }

  private HashMap<String, Object> prepareParam(
      final String title,
      final String description,
      final String image,
      final List<String> tagList) {
    return new HashMap<String, Object>() {
      {
        put(
            "item",
            new HashMap<String, Object>() {
              {
                put("title", title);
                put("description", description);
                put("image", image);
                put("tagList", tagList);
              }
            });
      }
    };
  }
}
