package io.spring.core.item;

import static java.util.stream.Collectors.toList;

import io.spring.Util;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Item {
  private String sellerId;
  private String id;
  private String slug;
  private String title;
  private String description;
  private String image;
  private List<Tag> tags;
  private DateTime createdAt;
  private DateTime updatedAt;

  public Item(
      String title, String description, String image, List<String> tagList, String sellerId) {
    this(title, description, image, tagList, sellerId, new DateTime());
  }

  public Item(
      String title,
      String description,
      String image,
      List<String> tagList,
      String sellerId,
      DateTime createdAt) {
    this.id = UUID.randomUUID().toString();
    this.slug = toSlug(title);
    this.title = title;
    this.description = description;
    this.image = image;
    this.tags = new HashSet<>(tagList).stream().map(Tag::new).collect(toList());
    this.sellerId = sellerId;
    this.createdAt = createdAt;
    this.updatedAt = createdAt;
  }

  public void update(String title, String description, String image) {
    if (!Util.isEmpty(title)) {
      this.title = title;
      this.slug = toSlug(title);
      this.updatedAt = new DateTime();
    }
    if (!Util.isEmpty(description)) {
      this.description = description;
      this.updatedAt = new DateTime();
    }
    if (!Util.isEmpty(image)) {
       this.image = image;
       this.updatedAt = new DateTime();
     }
  }

  public static String toSlug(String title) {
    return title.toLowerCase().replaceAll("[\\&|[\\uFE30-\\uFFA0]|\\’|\\”|\\s\\?\\,\\.]+", "-");
  }
}
