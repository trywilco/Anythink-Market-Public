package io.spring.infrastructure.mybatis.readservice;

import io.spring.application.data.ItemFavoriteCount;
import io.spring.core.user.User;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ItemFavoritesReadService {
  boolean isUserFavorite(@Param("userId") String userId, @Param("itemId") String itemId);

  int itemFavoriteCount(@Param("itemId") String itemId);

  List<ItemFavoriteCount> itemsFavoriteCount(@Param("ids") List<String> ids);

  Set<String> userFavorites(@Param("ids") List<String> ids, @Param("currentUser") User currentUser);
}
