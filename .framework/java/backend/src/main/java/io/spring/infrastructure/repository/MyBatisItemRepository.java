package io.spring.infrastructure.repository;

import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.item.Tag;
import io.spring.infrastructure.mybatis.mapper.ItemMapper;
import java.util.Optional;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MyBatisItemRepository implements ItemRepository {
  private ItemMapper itemMapper;

  public MyBatisItemRepository(ItemMapper itemMapper) {
    this.itemMapper = itemMapper;
  }

  @Override
  @Transactional
  public void save(Item item) {
    if (itemMapper.findById(item.getId()) == null) {
      createNew(item);
    } else {
      itemMapper.update(item);
    }
  }

  private void createNew(Item item) {
    for (Tag tag : item.getTags()) {
      Tag targetTag =
          Optional.ofNullable(itemMapper.findTag(tag.getName()))
              .orElseGet(
                  () -> {
                    itemMapper.insertTag(tag);
                    return tag;
                  });

      itemMapper.insertItemTagRelation(item.getId(), targetTag.getId(), new DateTime());
    }
    itemMapper.insert(item);
  }

  @Override
  public Optional<Item> findById(String id) {
    return Optional.ofNullable(itemMapper.findById(id));
  }

  @Override
  public Optional<Item> findBySlug(String slug) {
    return Optional.ofNullable(itemMapper.findBySlug(slug));
  }

  @Override
  public void remove(Item item) {
    itemMapper.delete(item.getId());
  }
}
