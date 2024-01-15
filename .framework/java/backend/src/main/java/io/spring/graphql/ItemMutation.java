package io.spring.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import graphql.execution.DataFetcherResult;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.item.ItemCommandService;
import io.spring.application.item.NewItemParam;
import io.spring.application.item.UpdateItemParam;
import io.spring.core.favorite.ItemFavorite;
import io.spring.core.favorite.ItemFavoriteRepository;
import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import io.spring.graphql.DgsConstants.MUTATION;
import io.spring.graphql.exception.AuthenticationException;
import io.spring.graphql.types.CreateItemInput;
import io.spring.graphql.types.DeletionStatus;
import io.spring.graphql.types.ItemPayload;
import io.spring.graphql.types.UpdateItemInput;
import java.util.Collections;
import lombok.AllArgsConstructor;

@DgsComponent
@AllArgsConstructor
public class ItemMutation {

  private ItemCommandService itemCommandService;
  private ItemFavoriteRepository itemFavoriteRepository;
  private ItemRepository itemRepository;

  @DgsMutation(field = MUTATION.CreateItem)
  public DataFetcherResult<ItemPayload> createItem(@InputArgument("input") CreateItemInput input) {
    User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);
    NewItemParam newItemParam =
        NewItemParam.builder()
            .title(input.getTitle())
            .description(input.getDescription())
            .image(input.getImage())
            .tagList(input.getTagList() == null ? Collections.emptyList() : input.getTagList())
            .build();
    Item item = itemCommandService.createItem(newItemParam, user);
    return DataFetcherResult.<ItemPayload>newResult()
        .data(ItemPayload.newBuilder().build())
        .localContext(item)
        .build();
  }

  @DgsMutation(field = MUTATION.UpdateItem)
  public DataFetcherResult<ItemPayload> updateItem(
      @InputArgument("slug") String slug, @InputArgument("changes") UpdateItemInput params) {
    Item item = itemRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);
    if (!AuthorizationService.canWriteItem(user, item)) {
      throw new NoAuthorizationException();
    }
    item =
        itemCommandService.updateItem(
            item,
            new UpdateItemParam(params.getTitle(), params.getImage(), params.getDescription()));
    return DataFetcherResult.<ItemPayload>newResult()
        .data(ItemPayload.newBuilder().build())
        .localContext(item)
        .build();
  }

  @DgsMutation(field = MUTATION.FavoriteItem)
  public DataFetcherResult<ItemPayload> favoriteItem(@InputArgument("slug") String slug) {
    User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);
    Item item = itemRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    ItemFavorite itemFavorite = new ItemFavorite(item.getId(), user.getId());
    itemFavoriteRepository.save(itemFavorite);
    return DataFetcherResult.<ItemPayload>newResult()
        .data(ItemPayload.newBuilder().build())
        .localContext(item)
        .build();
  }

  @DgsMutation(field = MUTATION.UnfavoriteItem)
  public DataFetcherResult<ItemPayload> unfavoriteItem(@InputArgument("slug") String slug) {
    User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);
    Item item = itemRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    itemFavoriteRepository
        .find(item.getId(), user.getId())
        .ifPresent(
            favorite -> {
              itemFavoriteRepository.remove(favorite);
            });
    return DataFetcherResult.<ItemPayload>newResult()
        .data(ItemPayload.newBuilder().build())
        .localContext(item)
        .build();
  }

  @DgsMutation(field = MUTATION.DeleteItem)
  public DeletionStatus deleteItem(@InputArgument("slug") String slug) {
    User user = SecurityUtil.getCurrentUser().orElseThrow(AuthenticationException::new);
    Item item = itemRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);

    if (!AuthorizationService.canWriteItem(user, item)) {
      throw new NoAuthorizationException();
    }

    itemRepository.remove(item);
    return DeletionStatus.newBuilder().success(true).build();
  }
}
