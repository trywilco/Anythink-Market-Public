package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.favorite.ItemFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ItemFavoriteMapper {
  ItemFavorite find(@Param("itemId") String itemId, @Param("userId") String userId);

  void insert(@Param("itemFavorite") ItemFavorite itemFavorite);

  void delete(@Param("favorite") ItemFavorite favorite);
}
