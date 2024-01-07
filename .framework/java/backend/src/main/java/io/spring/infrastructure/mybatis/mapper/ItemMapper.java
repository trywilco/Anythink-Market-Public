package io.spring.infrastructure.mybatis.mapper;

import io.spring.core.item.Item;
import io.spring.core.item.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ItemMapper {
  void insert(@Param("item") Item item);

  Item findById(@Param("id") String id);

  Tag findTag(@Param("tagName") String tagName);

  void insertTag(@Param("tag") Tag tag);

  void insertItemTagRelation(@Param("itemId") String itemId, @Param("tagId") String tagId);

  Item findBySlug(@Param("slug") String slug);

  void update(@Param("item") Item item);

  void delete(@Param("id") String id);
}
