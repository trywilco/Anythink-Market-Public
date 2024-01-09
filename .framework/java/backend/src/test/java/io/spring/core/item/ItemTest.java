package io.spring.core.item;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class ItemTest {

  @Test
  public void should_get_right_slug() {
    Item item = new Item("a new   title", "desc", "image", Arrays.asList("java"), "123");
    assertThat(item.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_get_right_slug_with_number_in_title() {
    Item item = new Item("a new title 2", "desc", "image", Arrays.asList("java"), "123");
    assertThat(item.getSlug(), is("a-new-title-2"));
  }

  @Test
  public void should_get_lower_case_slug() {
    Item item = new Item("A NEW TITLE", "desc", "image", Arrays.asList("java"), "123");
    assertThat(item.getSlug(), is("a-new-title"));
  }

  @Test
  public void should_handle_other_language() {
    Item item = new Item("中文：标题", "desc", "image", Arrays.asList("java"), "123");
    assertThat(item.getSlug(), is("中文-标题"));
  }

  @Test
  public void should_handle_commas() {
    Item item = new Item("what?the.hell,w", "desc", "image", Arrays.asList("java"), "123");
    assertThat(item.getSlug(), is("what-the-hell-w"));
  }
}
