package io.spring.infrastructure.item;

import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.item.Tag;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisItemRepository;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({MyBatisItemRepository.class, MyBatisUserRepository.class})
public class MyBatisItemRepositoryTest extends DbTestBase {
  @Autowired private ItemRepository itemRepository;

  @Autowired private UserRepository userRepository;

  private Item item;

  @BeforeEach
  public void setUp() {
    User user = new User("aisensiy@gmail.com", "aisensiy", "123", "bio", "default");
    userRepository.save(user);
    item = new Item("test", "desc", "image", "image", Arrays.asList("java", "spring"), user.getId());
  }

  @Test
  public void should_create_and_fetch_item_success() {
    itemRepository.save(item);
    Optional<Item> optional = itemRepository.findById(item.getId());
    Assertions.assertTrue(optional.isPresent());
    Assertions.assertEquals(optional.get(), item);
    Assertions.assertTrue(optional.get().getTags().contains(new Tag("java")));
    Assertions.assertTrue(optional.get().getTags().contains(new Tag("spring")));
  }

  @Test
  public void should_update_and_fetch_item_success() {
    itemRepository.save(item);

    String newTitle = "new test 2";
    item.update(newTitle, "", "");
    itemRepository.save(item);
    System.out.println(item.getSlug());
    Optional<Item> optional = itemRepository.findBySlug(item.getSlug());
    Assertions.assertTrue(optional.isPresent());
    Item fetched = optional.get();
    Assertions.assertEquals(fetched.getTitle(), newTitle);
    Assertions.assertNotEquals(fetched.getBody(), "");
  }

  @Test
  public void should_delete_item() {
    itemRepository.save(item);

    itemRepository.remove(item);
    Assertions.assertFalse(itemRepository.findById(item.getId()).isPresent());
  }
}
