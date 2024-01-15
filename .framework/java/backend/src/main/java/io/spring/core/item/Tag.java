package io.spring.core.item;

import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "name")
public class Tag {
  private String id;
  private String name;
  private DateTime createdAt;

  public Tag(String name) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.createdAt = new DateTime();
  }
}
