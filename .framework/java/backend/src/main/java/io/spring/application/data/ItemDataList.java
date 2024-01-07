package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class ItemDataList {
  @JsonProperty("items")
  private final List<ItemData> itemDatas;

  @JsonProperty("itemsCount")
  private final int count;

  public ItemDataList(List<ItemData> itemDatas, int count) {

    this.itemDatas = itemDatas;
    this.count = count;
  }
}
