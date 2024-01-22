package io.spring.core.comment;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Comment {
  private String id;
  private String body;
  private String sellerId;
  private String itemId;
  private DateTime createdAt;

  public Comment(String body, String sellerId, String itemId) {
    this.id = UUID.randomUUID().toString();
    this.body = body;
    this.sellerId = sellerId;
    this.itemId = itemId;
    this.createdAt = new DateTime();
  }
}
