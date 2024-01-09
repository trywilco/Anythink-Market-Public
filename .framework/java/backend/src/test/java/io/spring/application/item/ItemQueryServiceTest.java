package io.spring.application.item;

import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.DateTimeCursor;
import io.spring.application.ItemQueryService;
import io.spring.application.Page;
import io.spring.application.data.ItemData;
import io.spring.application.data.ItemDataList;
import io.spring.core.favorite.ItemFavorite;
import io.spring.core.favorite.ItemFavoriteRepository;
import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisItemFavoriteRepository;
import io.spring.infrastructure.repository.MyBatisItemRepository;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import java.util.Arrays;
import java.util.Optional;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({
  ItemQueryService.class,
  MyBatisUserRepository.class,
  MyBatisItemRepository.class,
  MyBatisItemFavoriteRepository.class
})
public class ItemQueryServiceTest extends DbTestBase {
  @Autowired private ItemQueryService queryService;

  @Autowired private ItemRepository itemRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ItemFavoriteRepository itemFavoriteRepository;

  private User user;
  private Item item;

  @BeforeEach
  public void setUp() {
    user = new User("aisensiy@gmail.com", "aisensiy", "123", "", "");
    userRepository.save(user);
    item =
        new Item(
            "test", "desc", "image", Arrays.asList("java", "spring"), user.getId(), new DateTime());
    itemRepository.save(item);
  }

  @Test
  public void should_fetch_item_success() {
    Optional<ItemData> optional = queryService.findById(item.getId(), user);
    Assertions.assertTrue(optional.isPresent());

    ItemData fetched = optional.get();
    Assertions.assertEquals(fetched.getFavoritesCount(), 0);
    Assertions.assertFalse(fetched.isFavorited());
    Assertions.assertNotNull(fetched.getCreatedAt());
    Assertions.assertNotNull(fetched.getUpdatedAt());
    Assertions.assertTrue(fetched.getTagList().contains("java"));
  }

  @Test
  public void should_get_item_with_right_favorite_and_favorite_count() {
    User anotherUser = new User("other@test.com", "other", "123", "", "");
    userRepository.save(anotherUser);
    itemFavoriteRepository.save(new ItemFavorite(item.getId(), anotherUser.getId()));

    Optional<ItemData> optional = queryService.findById(item.getId(), anotherUser);
    Assertions.assertTrue(optional.isPresent());

    ItemData itemData = optional.get();
    Assertions.assertEquals(itemData.getFavoritesCount(), 1);
    Assertions.assertTrue(itemData.isFavorited());
  }

  @Test
  public void should_get_default_item_list() {
    Item anotherItem =
        new Item(
            "new item",
            "desc",
            "image",
            Arrays.asList("test"),
            user.getId(),
            new DateTime().minusHours(1));
    itemRepository.save(anotherItem);

    ItemDataList recentItems = queryService.findRecentItems(null, null, null, new Page(), user);
    Assertions.assertEquals(recentItems.getCount(), 2);
    Assertions.assertEquals(recentItems.getItemDatas().size(), 2);
    Assertions.assertEquals(recentItems.getItemDatas().get(0).getId(), item.getId());

    ItemDataList nodata = queryService.findRecentItems(null, null, null, new Page(2, 10), user);
    Assertions.assertEquals(nodata.getCount(), 2);
    Assertions.assertEquals(nodata.getItemDatas().size(), 0);
  }

  @Test
  public void should_get_default_item_list_by_cursor() {
    Item anotherItem =
        new Item(
            "new item",
            "desc",
            "image",
            Arrays.asList("test"),
            user.getId(),
            new DateTime().minusHours(1));
    itemRepository.save(anotherItem);

    CursorPager<ItemData> recentItems =
        queryService.findRecentItemsWithCursor(
            null, null, null, new CursorPageParameter<>(null, 20, Direction.NEXT), user);
    Assertions.assertEquals(recentItems.getData().size(), 2);
    Assertions.assertEquals(recentItems.getData().get(0).getId(), item.getId());

    CursorPager<ItemData> nodata =
        queryService.findRecentItemsWithCursor(
            null,
            null,
            null,
            new CursorPageParameter<DateTime>(
                DateTimeCursor.parse(recentItems.getEndCursor().toString()), 20, Direction.NEXT),
            user);
    Assertions.assertEquals(nodata.getData().size(), 0);
    Assertions.assertEquals(nodata.getStartCursor(), null);

    CursorPager<ItemData> prevItems =
        queryService.findRecentItemsWithCursor(
            null, null, null, new CursorPageParameter<>(null, 20, Direction.PREV), user);
    Assertions.assertEquals(prevItems.getData().size(), 2);
  }

  @Test
  public void should_query_item_by_seller() {
    User anotherUser = new User("other@email.com", "other", "123", "", "");
    userRepository.save(anotherUser);

    Item anotherItem =
        new Item("new item", "desc", "image", Arrays.asList("test"), anotherUser.getId());
    itemRepository.save(anotherItem);

    ItemDataList recentItems =
        queryService.findRecentItems(null, user.getUsername(), null, new Page(), user);
    Assertions.assertEquals(recentItems.getItemDatas().size(), 1);
    Assertions.assertEquals(recentItems.getCount(), 1);
  }

  @Test
  public void should_query_item_by_favorite() {
    User anotherUser = new User("other@email.com", "other", "123", "", "");
    userRepository.save(anotherUser);

    Item anotherItem =
        new Item("new item", "desc", "image", Arrays.asList("test"), anotherUser.getId());
    itemRepository.save(anotherItem);

    ItemFavorite itemFavorite = new ItemFavorite(item.getId(), anotherUser.getId());
    itemFavoriteRepository.save(itemFavorite);

    ItemDataList recentItems =
        queryService.findRecentItems(
            null, null, anotherUser.getUsername(), new Page(), anotherUser);
    Assertions.assertEquals(recentItems.getItemDatas().size(), 1);
    Assertions.assertEquals(recentItems.getCount(), 1);
    ItemData itemData = recentItems.getItemDatas().get(0);
    Assertions.assertEquals(itemData.getId(), item.getId());
    Assertions.assertEquals(itemData.getFavoritesCount(), 1);
    Assertions.assertTrue(itemData.isFavorited());
  }

  @Test
  public void should_query_item_by_tag() {
    Item anotherItem = new Item("new item", "desc", "image", Arrays.asList("test"), user.getId());
    itemRepository.save(anotherItem);

    ItemDataList recentItems = queryService.findRecentItems("spring", null, null, new Page(), user);
    Assertions.assertEquals(recentItems.getItemDatas().size(), 1);
    Assertions.assertEquals(recentItems.getCount(), 1);
    Assertions.assertEquals(recentItems.getItemDatas().get(0).getId(), item.getId());

    ItemDataList notag = queryService.findRecentItems("notag", null, null, new Page(), user);
    Assertions.assertEquals(notag.getCount(), 0);
  }

  @Test
  public void should_show_following_if_user_followed_seller() {
    User anotherUser = new User("other@email.com", "other", "123", "", "");
    userRepository.save(anotherUser);

    FollowRelation followRelation = new FollowRelation(anotherUser.getId(), user.getId());
    userRepository.saveRelation(followRelation);

    ItemDataList recentItems =
        queryService.findRecentItems(null, null, null, new Page(), anotherUser);
    Assertions.assertEquals(recentItems.getCount(), 1);
    ItemData itemData = recentItems.getItemDatas().get(0);
    Assertions.assertTrue(itemData.getProfileData().isFollowing());
  }

  @Test
  public void should_get_user_feed() {
    User anotherUser = new User("other@email.com", "other", "123", "", "");
    userRepository.save(anotherUser);

    FollowRelation followRelation = new FollowRelation(anotherUser.getId(), user.getId());
    userRepository.saveRelation(followRelation);

    ItemDataList userFeed = queryService.findUserFeed(user, new Page());
    Assertions.assertEquals(userFeed.getCount(), 0);

    ItemDataList anotherUserFeed = queryService.findUserFeed(anotherUser, new Page());
    Assertions.assertEquals(anotherUserFeed.getCount(), 1);
    ItemData itemData = anotherUserFeed.getItemDatas().get(0);
    Assertions.assertTrue(itemData.getProfileData().isFollowing());
  }
}
