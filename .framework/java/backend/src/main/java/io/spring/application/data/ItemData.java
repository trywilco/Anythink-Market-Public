package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.application.DateTimeCursor;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemData implements io.spring.application.Node {
  private String id;
  private String slug;
  private String title;
  private String description;
  private String image;
  private boolean favorited;
  private int favoritesCount;
  private DateTime createdAt;
  private DateTime updatedAt;
  private List<String> tagList;

  @JsonProperty("seller")
  private ProfileData profileData;

  @Override
  public DateTimeCursor getCursor() {
    return new DateTimeCursor(updatedAt);
  }
}
