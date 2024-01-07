package io.spring;

import io.spring.application.data.ItemData;
import io.spring.application.data.ProfileData;
import io.spring.core.item.Item;
import io.spring.core.user.User;
import java.util.ArrayList;
import java.util.Arrays;
import org.joda.time.DateTime;

public class TestHelper {
  public static ItemData itemDataFixture(String seed, User user) {
    DateTime now = new DateTime();
    return new ItemData(
        seed + "id",
        "title-" + seed,
        "title " + seed,
        "desc " + seed,
        "image" + seed,
        false,
        0,
        now,
        now,
        new ArrayList<>(),
        new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false));
  }

  public static ItemData getItemDataFromItemAndUser(Item item, User user) {
    return new ItemData(
        item.getId(),
        item.getSlug(),
        item.getTitle(),
        item.getDescription(),
        item.getImage(),
        false,
        0,
        item.getCreatedAt(),
        item.getUpdatedAt(),
        Arrays.asList("joda"),
        new ProfileData(user.getId(), user.getUsername(), user.getBio(), user.getImage(), false));
  }
}
