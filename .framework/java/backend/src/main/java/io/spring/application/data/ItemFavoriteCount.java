package io.spring.application.data;

import lombok.Value;

@Value
public class ItemFavoriteCount {
  private String id;
  private Integer count;
}
