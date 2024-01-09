package io.spring.infrastructure.mybatis.readservice;

import io.spring.application.CursorPageParameter;
import io.spring.application.Page;
import io.spring.application.data.ItemData;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ItemReadService {
  ItemData findById(@Param("id") String id);

  ItemData findBySlug(@Param("slug") String slug);

  List<String> queryItems(
      @Param("tag") String tag,
      @Param("seller") String seller,
      @Param("favoritedBy") String favoritedBy,
      @Param("page") Page page);

  int countItem(
      @Param("tag") String tag,
      @Param("seller") String seller,
      @Param("favoritedBy") String favoritedBy);

  List<ItemData> findItems(@Param("itemIds") List<String> itemIds);

  List<ItemData> findItemsOfSellers(
      @Param("sellers") List<String> authors, @Param("page") Page page);

  List<ItemData> findItemsOfSellersWithCursor(
      @Param("sellers") List<String> authors, @Param("page") CursorPageParameter page);

  int countFeedSize(@Param("sellers") List<String> sellers);

  List<String> findItemsWithCursor(
      @Param("tag") String tag,
      @Param("seller") String seller,
      @Param("favoritedBy") String favoritedBy,
      @Param("page") CursorPageParameter page);
}
