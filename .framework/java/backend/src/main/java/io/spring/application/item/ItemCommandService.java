package io.spring.application.item;

import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.user.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@AllArgsConstructor
public class ItemCommandService {

  private ItemRepository itemRepository;

  public Item createItem(@Valid NewItemParam newItemParam, User creator) {
    Item item =
        new Item(
            newItemParam.getTitle(),
            newItemParam.getDescription(),
            newItemParam.getImage(),
            newItemParam.getTagList(),
            creator.getId());
    itemRepository.save(item);
    return item;
  }

  public Item updateItem(Item item, @Valid UpdateItemParam updateItemParam) {
    item.update(
        updateItemParam.getTitle(), updateItemParam.getDescription(), updateItemParam.getImage());
    itemRepository.save(item);
    return item;
  }
}
