package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.spring.TestHelper.itemDataFixture;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.ItemQueryService;
import io.spring.application.Page;
import io.spring.application.data.ItemDataList;
import io.spring.application.item.ItemCommandService;
import io.spring.core.item.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ItemsApi.class)
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class ListItemApiTest extends TestWithCurrentUser {
  @MockBean private ItemRepository itemRepository;

  @MockBean private ItemQueryService itemQueryService;

  @MockBean private ItemCommandService itemCommandService;

  @Autowired private MockMvc mvc;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_get_default_item_list() throws Exception {
    ItemDataList itemDataList =
        new ItemDataList(asList(itemDataFixture("1", user), itemDataFixture("2", user)), 2);
    when(itemQueryService.findRecentItems(
            eq(null), eq(null), eq(null), eq(new Page(0, 20)), eq(null)))
        .thenReturn(itemDataList);
    RestAssuredMockMvc.when().get("/api/items").prettyPeek().then().statusCode(200);
  }

  @Test
  public void should_get_feeds_401_without_login() throws Exception {
    RestAssuredMockMvc.when().get("/api/items/feed").prettyPeek().then().statusCode(401);
  }

  @Test
  public void should_get_feeds_success() throws Exception {
    ItemDataList itemDataList =
        new ItemDataList(asList(itemDataFixture("1", user), itemDataFixture("2", user)), 2);
    when(itemQueryService.findUserFeed(eq(user), eq(new Page(0, 20)))).thenReturn(itemDataList);

    given()
        .header("Authorization", "Token " + token)
        .when()
        .get("/api/items/feed")
        .prettyPeek()
        .then()
        .statusCode(200);
  }
}
