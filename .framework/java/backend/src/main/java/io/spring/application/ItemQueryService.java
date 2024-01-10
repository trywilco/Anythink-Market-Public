package io.spring.application;

import static java.util.stream.Collectors.toList;

import io.spring.application.data.ItemData;
import io.spring.application.data.ItemDataList;
import io.spring.application.data.ItemFavoriteCount;
import io.spring.core.user.User;
import io.spring.infrastructure.mybatis.readservice.ItemFavoritesReadService;
import io.spring.infrastructure.mybatis.readservice.ItemReadService;
import io.spring.infrastructure.mybatis.readservice.UserRelationshipQueryService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ItemQueryService {
  private ItemReadService itemReadService;
  private UserRelationshipQueryService userRelationshipQueryService;
  private ItemFavoritesReadService itemFavoritesReadService;

  public Optional<ItemData> findById(String id, User user) {
    ItemData itemData = itemReadService.findById(id);
    if (itemData == null) {
      return Optional.empty();
    } else {
      if (user != null) {
        fillExtraInfo(id, user, itemData);
      }
      return Optional.of(itemData);
    }
  }

  public Optional<ItemData> findBySlug(String slug, User user) {
    ItemData itemData = itemReadService.findBySlug(slug);
    if (itemData == null) {
      return Optional.empty();
    } else {
      if (user != null) {
        fillExtraInfo(itemData.getId(), user, itemData);
      }

      setFavoriteCount(Collections.singletonList(itemData));
      return Optional.of(itemData);
    }
  }

  public CursorPager<ItemData> findRecentItemsWithCursor(
      String tag,
      String seller,
      String favoritedBy,
      CursorPageParameter<DateTime> page,
      User currentUser) {
    List<String> itemIds = itemReadService.findItemsWithCursor(tag, seller, favoritedBy, page);
    if (itemIds.size() == 0) {
      return new CursorPager<>(new ArrayList<>(), page.getDirection(), false);
    } else {
      boolean hasExtra = itemIds.size() > page.getLimit();
      if (hasExtra) {
        itemIds.remove(page.getLimit());
      }
      if (!page.isNext()) {
        Collections.reverse(itemIds);
      }

      List<ItemData> items = itemReadService.findItems(itemIds);
      fillExtraInfo(items, currentUser);

      return new CursorPager<>(items, page.getDirection(), hasExtra);
    }
  }

  public CursorPager<ItemData> findUserFeedWithCursor(
      User user, CursorPageParameter<DateTime> page) {
    List<String> followedUsers = userRelationshipQueryService.followedUsers(user.getId());
    if (followedUsers.size() == 0) {
      return new CursorPager<>(new ArrayList<>(), page.getDirection(), false);
    } else {
      List<ItemData> items = itemReadService.findItemsOfSellersWithCursor(followedUsers, page);
      boolean hasExtra = items.size() > page.getLimit();
      if (hasExtra) {
        items.remove(page.getLimit());
      }
      if (!page.isNext()) {
        Collections.reverse(items);
      }
      fillExtraInfo(items, user);
      return new CursorPager<>(items, page.getDirection(), hasExtra);
    }
  }

  public ItemDataList findRecentItems(
      String tag, String seller, String favoritedBy, Page page, User currentUser) {
    List<String> itemIds = itemReadService.queryItems(tag, seller, favoritedBy, page);
    int itemCount = itemReadService.countItem(tag, seller, favoritedBy);
    if (itemIds.size() == 0) {
      return new ItemDataList(new ArrayList<>(), itemCount);
    } else {
      List<ItemData> items = itemReadService.findItems(itemIds);
      fillExtraInfo(items, currentUser);
      return new ItemDataList(items, itemCount);
    }
  }

  public ItemDataList findUserFeed(User user, Page page) {
    List<String> followedUsers = userRelationshipQueryService.followedUsers(user.getId());
    if (followedUsers.size() == 0) {
      return new ItemDataList(new ArrayList<>(), 0);
    } else {
      List<ItemData> items = itemReadService.findItemsOfSellers(followedUsers, page);
      int offset = page.getOffset();
      int limit = page.getLimit();
      int endIndex = Math.min(offset + limit, items.size());
      items = items.subList(offset, endIndex);

      fillExtraInfo(items, user);
      int count = itemReadService.countFeedSize(followedUsers);
      return new ItemDataList(items, count);
    }
  }

  private void fillExtraInfo(List<ItemData> items, User currentUser) {
    setFavoriteCount(items);
    if (currentUser != null) {
      setIsFavorite(items, currentUser);
      setIsFollowingSeller(items, currentUser);
    }
  }

  private void setIsFollowingSeller(List<ItemData> items, User currentUser) {
    Set<String> followingSellers =
        userRelationshipQueryService.followingSellers(
            currentUser.getId(),
            items.stream().map(itemData1 -> itemData1.getProfileData().getId()).collect(toList()));
    items.forEach(
        itemData -> {
          if (followingSellers.contains(itemData.getProfileData().getId())) {
            itemData.getProfileData().setFollowing(true);
          }
        });
  }

  private void setFavoriteCount(List<ItemData> items) {
    List<ItemFavoriteCount> favoritesCounts =
        itemFavoritesReadService.itemsFavoriteCount(
            items.stream().map(ItemData::getId).collect(toList()));
    Map<String, Integer> countMap = new HashMap<>();
    favoritesCounts.forEach(
        item -> {
          countMap.put(item.getId(), item.getCount());
        });
    items.forEach(itemData -> itemData.setFavoritesCount(countMap.get(itemData.getId())));
  }

  private void setIsFavorite(List<ItemData> items, User currentUser) {
    Set<String> favoritedItems =
        itemFavoritesReadService.userFavorites(
            items.stream().map(itemData -> itemData.getId()).collect(toList()), currentUser);

    items.forEach(
        itemData -> {
          if (favoritedItems.contains(itemData.getId())) {
            itemData.setFavorited(true);
          }
        });
  }

  private void fillExtraInfo(String id, User user, ItemData itemData) {
    itemData.setFavorited(itemFavoritesReadService.isUserFavorite(user.getId(), id));
    itemData.setFavoritesCount(itemFavoritesReadService.itemFavoriteCount(id));
    itemData
        .getProfileData()
        .setFollowing(
            userRelationshipQueryService.isUserFollowing(
                user.getId(), itemData.getProfileData().getId()));
  }
}
