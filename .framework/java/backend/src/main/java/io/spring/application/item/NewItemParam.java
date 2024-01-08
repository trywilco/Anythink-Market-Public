package io.spring.application.item;

import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonRootName("item")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewItemParam {
  @NotBlank(message = "can't be empty")
  @DuplicatedItemConstraint
  private String title;

  @NotBlank(message = "can't be empty")
  private String description;

  private String image;

  private List<String> tagList;
}
