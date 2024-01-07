package io.spring.application.item;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("item")
public class UpdateItemParam {
  private String title = "";
  private String image = "";
  private String description = "";
}
