package io.spring.application.tag;

import io.spring.application.TagsQueryService;
import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisItemRepository;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({TagsQueryService.class, MyBatisItemRepository.class})
public class TagsQueryServiceTest extends DbTestBase {
  @Autowired private TagsQueryService tagsQueryService;

  @Autowired private ItemRepository itemRepository;

  @Test
  public void should_get_all_tags() {
    itemRepository.save(new Item("test", "test", "image", Arrays.asList("java"), "123"));
    Assertions.assertTrue(tagsQueryService.allTags().contains("java"));
  }
}
