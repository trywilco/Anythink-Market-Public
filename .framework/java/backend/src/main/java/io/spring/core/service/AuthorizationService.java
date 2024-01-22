package io.spring.core.service;

import io.spring.core.comment.Comment;
import io.spring.core.item.Item;
import io.spring.core.user.User;

public class AuthorizationService {
  public static boolean canWriteItem(User user, Item item) {
    return user.getId().equals(item.getSellerId());
  }

  public static boolean canWriteComment(User user, Item item, Comment comment) {
    return user.getId().equals(item.getSellerId()) || user.getId().equals(comment.getSellerId());
  }
}
