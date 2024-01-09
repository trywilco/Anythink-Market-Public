package io.spring.infrastructure.item;

import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.mapper.ItemMapper;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRepositoryTransactionTest {
  @Autowired private ItemRepository itemRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ItemMapper itemMapper;

  @Test
  public void transactional_test() {
    User user = new User("aisensiy@gmail.com", "aisensiy", "123", "bio", "default");
    userRepository.save(user);
    Item item = new Item("test", "desc", "image", Arrays.asList("java", "spring"), user.getId());
    itemRepository.save(item);
    Item anotherItem =
        new Item("test", "desc", "image", Arrays.asList("java", "spring", "other"), user.getId());
    try {
      itemRepository.save(anotherItem);
    } catch (Exception e) {
      Assertions.assertNull(itemMapper.findTag("other"));
    }
  }
}
