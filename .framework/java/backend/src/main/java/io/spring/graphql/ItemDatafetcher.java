package io.spring.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import graphql.execution.DataFetcherResult;
import graphql.relay.DefaultConnectionCursor;
import graphql.relay.DefaultPageInfo;
import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CursorPageParameter;
import io.spring.application.CursorPager;
import io.spring.application.CursorPager.Direction;
import io.spring.application.DateTimeCursor;
import io.spring.application.ItemQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ItemData;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.DgsConstants.COMMENT;
import io.spring.graphql.DgsConstants.ITEMPAYLOAD;
import io.spring.graphql.DgsConstants.PROFILE;
import io.spring.graphql.DgsConstants.QUERY;
import io.spring.graphql.types.Item;
import io.spring.graphql.types.ItemEdge;
import io.spring.graphql.types.ItemsConnection;
import io.spring.graphql.types.Profile;
import java.util.HashMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.joda.time.format.ISODateTimeFormat;

@DgsComponent
@AllArgsConstructor
public class ItemDatafetcher {

  private ItemQueryService itemQueryService;
  private UserRepository userRepository;

  @DgsQuery(field = QUERY.Feed)
  public DataFetcherResult<ItemsConnection> getFeed(
      @InputArgument("first") Integer first,
      @InputArgument("after") String after,
      @InputArgument("last") Integer last,
      @InputArgument("before") String before,
      DgsDataFetchingEnvironment dfe) {
    if (first == null && last == null) {
      throw new IllegalArgumentException("first 和 last 必须只存在一个");
    }

    User current = SecurityUtil.getCurrentUser().orElse(null);

    CursorPager<ItemData> items;
    if (first != null) {
      items =
          itemQueryService.findUserFeedWithCursor(
              current,
              new CursorPageParameter<>(DateTimeCursor.parse(after), first, Direction.NEXT));
    } else {
      items =
          itemQueryService.findUserFeedWithCursor(
              current,
              new CursorPageParameter<>(DateTimeCursor.parse(before), last, Direction.PREV));
    }
    graphql.relay.PageInfo pageInfo = buildItemPageInfo(items);
    ItemsConnection itemsConnection =
        ItemsConnection.newBuilder()
            .pageInfo(pageInfo)
            .edges(
                items.getData().stream()
                    .map(
                        a ->
                            ItemEdge.newBuilder()
                                .cursor(a.getCursor().toString())
                                .node(buildItemResult(a))
                                .build())
                    .collect(Collectors.toList()))
            .build();
    return DataFetcherResult.<ItemsConnection>newResult()
        .data(itemsConnection)
        .localContext(items.getData().stream().collect(Collectors.toMap(ItemData::getSlug, a -> a)))
        .build();
  }

  @DgsData(parentType = PROFILE.TYPE_NAME, field = PROFILE.Feed)
  public DataFetcherResult<ItemsConnection> userFeed(
      @InputArgument("first") Integer first,
      @InputArgument("after") String after,
      @InputArgument("last") Integer last,
      @InputArgument("before") String before,
      DgsDataFetchingEnvironment dfe) {
    if (first == null && last == null) {
      throw new IllegalArgumentException("first 和 last 必须只存在一个");
    }

    Profile profile = dfe.getSource();
    User target =
        userRepository
            .findByUsername(profile.getUsername())
            .orElseThrow(ResourceNotFoundException::new);

    CursorPager<ItemData> items;
    if (first != null) {
      items =
          itemQueryService.findUserFeedWithCursor(
              target,
              new CursorPageParameter<>(DateTimeCursor.parse(after), first, Direction.NEXT));
    } else {
      items =
          itemQueryService.findUserFeedWithCursor(
              target,
              new CursorPageParameter<>(DateTimeCursor.parse(before), last, Direction.PREV));
    }
    graphql.relay.PageInfo pageInfo = buildItemPageInfo(items);
    ItemsConnection itemsConnection =
        ItemsConnection.newBuilder()
            .pageInfo(pageInfo)
            .edges(
                items.getData().stream()
                    .map(
                        a ->
                            ItemEdge.newBuilder()
                                .cursor(a.getCursor().toString())
                                .node(buildItemResult(a))
                                .build())
                    .collect(Collectors.toList()))
            .build();
    return DataFetcherResult.<ItemsConnection>newResult()
        .data(itemsConnection)
        .localContext(items.getData().stream().collect(Collectors.toMap(ItemData::getSlug, a -> a)))
        .build();
  }

  @DgsData(parentType = PROFILE.TYPE_NAME, field = PROFILE.Favorites)
  public DataFetcherResult<ItemsConnection> userFavorites(
      @InputArgument("first") Integer first,
      @InputArgument("after") String after,
      @InputArgument("last") Integer last,
      @InputArgument("before") String before,
      DgsDataFetchingEnvironment dfe) {
    if (first == null && last == null) {
      throw new IllegalArgumentException("first 和 last 必须只存在一个");
    }

    User current = SecurityUtil.getCurrentUser().orElse(null);
    Profile profile = dfe.getSource();

    CursorPager<ItemData> items;
    if (first != null) {
      items =
          itemQueryService.findRecentItemsWithCursor(
              null,
              null,
              profile.getUsername(),
              new CursorPageParameter<>(DateTimeCursor.parse(after), first, Direction.NEXT),
              current);
    } else {
      items =
          itemQueryService.findRecentItemsWithCursor(
              null,
              null,
              profile.getUsername(),
              new CursorPageParameter<>(DateTimeCursor.parse(before), last, Direction.PREV),
              current);
    }
    graphql.relay.PageInfo pageInfo = buildItemPageInfo(items);

    ItemsConnection itemsConnection =
        ItemsConnection.newBuilder()
            .pageInfo(pageInfo)
            .edges(
                items.getData().stream()
                    .map(
                        a ->
                            ItemEdge.newBuilder()
                                .cursor(a.getCursor().toString())
                                .node(buildItemResult(a))
                                .build())
                    .collect(Collectors.toList()))
            .build();
    return DataFetcherResult.<ItemsConnection>newResult()
        .data(itemsConnection)
        .localContext(items.getData().stream().collect(Collectors.toMap(ItemData::getSlug, a -> a)))
        .build();
  }

  @DgsData(parentType = PROFILE.TYPE_NAME, field = PROFILE.Items)
  public DataFetcherResult<ItemsConnection> userItems(
      @InputArgument("first") Integer first,
      @InputArgument("after") String after,
      @InputArgument("last") Integer last,
      @InputArgument("before") String before,
      DgsDataFetchingEnvironment dfe) {
    if (first == null && last == null) {
      throw new IllegalArgumentException("first 和 last 必须只存在一个");
    }

    User current = SecurityUtil.getCurrentUser().orElse(null);
    Profile profile = dfe.getSource();

    CursorPager<ItemData> items;
    if (first != null) {
      items =
          itemQueryService.findRecentItemsWithCursor(
              null,
              profile.getUsername(),
              null,
              new CursorPageParameter<>(DateTimeCursor.parse(after), first, Direction.NEXT),
              current);
    } else {
      items =
          itemQueryService.findRecentItemsWithCursor(
              null,
              profile.getUsername(),
              null,
              new CursorPageParameter<>(DateTimeCursor.parse(before), last, Direction.PREV),
              current);
    }
    graphql.relay.PageInfo pageInfo = buildItemPageInfo(items);
    ItemsConnection itemsConnection =
        ItemsConnection.newBuilder()
            .pageInfo(pageInfo)
            .edges(
                items.getData().stream()
                    .map(
                        a ->
                            ItemEdge.newBuilder()
                                .cursor(a.getCursor().toString())
                                .node(buildItemResult(a))
                                .build())
                    .collect(Collectors.toList()))
            .build();
    return DataFetcherResult.<ItemsConnection>newResult()
        .data(itemsConnection)
        .localContext(items.getData().stream().collect(Collectors.toMap(ItemData::getSlug, a -> a)))
        .build();
  }

  @DgsData(parentType = DgsConstants.QUERY_TYPE, field = QUERY.Items)
  public DataFetcherResult<ItemsConnection> getItems(
      @InputArgument("first") Integer first,
      @InputArgument("after") String after,
      @InputArgument("last") Integer last,
      @InputArgument("before") String before,
      @InputArgument("soldBy") String soldBy,
      @InputArgument("favoritedBy") String favoritedBy,
      @InputArgument("withTag") String withTag,
      DgsDataFetchingEnvironment dfe) {
    if (first == null && last == null) {
      throw new IllegalArgumentException("first 和 last 必须只存在一个");
    }

    User current = SecurityUtil.getCurrentUser().orElse(null);

    CursorPager<ItemData> items;
    if (first != null) {
      items =
          itemQueryService.findRecentItemsWithCursor(
              withTag,
              soldBy,
              favoritedBy,
              new CursorPageParameter<>(DateTimeCursor.parse(after), first, Direction.NEXT),
              current);
    } else {
      items =
          itemQueryService.findRecentItemsWithCursor(
              withTag,
              soldBy,
              favoritedBy,
              new CursorPageParameter<>(DateTimeCursor.parse(before), last, Direction.PREV),
              current);
    }
    graphql.relay.PageInfo pageInfo = buildItemPageInfo(items);
    ItemsConnection itemsConnection =
        ItemsConnection.newBuilder()
            .pageInfo(pageInfo)
            .edges(
                items.getData().stream()
                    .map(
                        a ->
                            ItemEdge.newBuilder()
                                .cursor(a.getCursor().toString())
                                .node(buildItemResult(a))
                                .build())
                    .collect(Collectors.toList()))
            .build();
    return DataFetcherResult.<ItemsConnection>newResult()
        .data(itemsConnection)
        .localContext(items.getData().stream().collect(Collectors.toMap(ItemData::getSlug, a -> a)))
        .build();
  }

  @DgsData(parentType = ITEMPAYLOAD.TYPE_NAME, field = ITEMPAYLOAD.Item)
  public DataFetcherResult<Item> getItem(DataFetchingEnvironment dfe) {
    io.spring.core.item.Item item = dfe.getLocalContext();

    User current = SecurityUtil.getCurrentUser().orElse(null);
    ItemData itemData =
        itemQueryService
            .findById(item.getId(), current)
            .orElseThrow(ResourceNotFoundException::new);
    Item itemResult = buildItemResult(itemData);
    return DataFetcherResult.<Item>newResult()
        .localContext(
            new HashMap<String, Object>() {
              {
                put(itemData.getSlug(), itemData);
              }
            })
        .data(itemResult)
        .build();
  }

  @DgsData(parentType = COMMENT.TYPE_NAME, field = COMMENT.Item)
  public DataFetcherResult<Item> getCommentItem(DataFetchingEnvironment dataFetchingEnvironment) {
    CommentData comment = dataFetchingEnvironment.getLocalContext();
    User current = SecurityUtil.getCurrentUser().orElse(null);
    ItemData itemData =
        itemQueryService
            .findById(comment.getItemId(), current)
            .orElseThrow(ResourceNotFoundException::new);
    Item itemResult = buildItemResult(itemData);
    return DataFetcherResult.<Item>newResult()
        .localContext(
            new HashMap<String, Object>() {
              {
                put(itemData.getSlug(), itemData);
              }
            })
        .data(itemResult)
        .build();
  }

  @DgsQuery(field = QUERY.Item)
  public DataFetcherResult<Item> findItemBySlug(@InputArgument("slug") String slug) {
    User current = SecurityUtil.getCurrentUser().orElse(null);
    ItemData itemData =
        itemQueryService.findBySlug(slug, current).orElseThrow(ResourceNotFoundException::new);
    Item itemResult = buildItemResult(itemData);
    return DataFetcherResult.<Item>newResult()
        .localContext(
            new HashMap<String, Object>() {
              {
                put(itemData.getSlug(), itemData);
              }
            })
        .data(itemResult)
        .build();
  }

  private DefaultPageInfo buildItemPageInfo(CursorPager<ItemData> items) {
    return new DefaultPageInfo(
        items.getStartCursor() == null
            ? null
            : new DefaultConnectionCursor(items.getStartCursor().toString()),
        items.getEndCursor() == null
            ? null
            : new DefaultConnectionCursor(items.getEndCursor().toString()),
        items.hasPrevious(),
        items.hasNext());
  }

  private Item buildItemResult(ItemData itemData) {
    return Item.newBuilder()
        .image(itemData.getImage())
        .createdAt(ISODateTimeFormat.dateTime().withZoneUTC().print(itemData.getCreatedAt()))
        .description(itemData.getDescription())
        .favorited(itemData.isFavorited())
        .favoritesCount(itemData.getFavoritesCount())
        .slug(itemData.getSlug())
        .tagList(itemData.getTagList())
        .title(itemData.getTitle())
        .updatedAt(ISODateTimeFormat.dateTime().withZoneUTC().print(itemData.getUpdatedAt()))
        .build();
  }
}
