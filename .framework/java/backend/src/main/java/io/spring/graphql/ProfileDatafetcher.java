package io.spring.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ItemData;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.user.User;
import io.spring.graphql.DgsConstants.ITEM;
import io.spring.graphql.DgsConstants.COMMENT;
import io.spring.graphql.DgsConstants.QUERY;
import io.spring.graphql.DgsConstants.USER;
import io.spring.graphql.types.Item;
import io.spring.graphql.types.Comment;
import io.spring.graphql.types.Profile;
import io.spring.graphql.types.ProfilePayload;
import java.util.Map;
import lombok.AllArgsConstructor;

@DgsComponent
@AllArgsConstructor
public class ProfileDatafetcher {

  private ProfileQueryService profileQueryService;

  @DgsData(parentType = USER.TYPE_NAME, field = USER.Profile)
  public Profile getUserProfile(DataFetchingEnvironment dataFetchingEnvironment) {
    User user = dataFetchingEnvironment.getLocalContext();
    String username = user.getUsername();
    return queryProfile(username);
  }

  @DgsData(parentType = ITEM.TYPE_NAME, field = ITEM.Seller)
  public Profile getSeller(DataFetchingEnvironment dataFetchingEnvironment) {
    Map<String, ItemData> map = dataFetchingEnvironment.getLocalContext();
    Item item = dataFetchingEnvironment.getSource();
    return queryProfile(map.get(item.getSlug()).getProfileData().getUsername());
  }

  @DgsData(parentType = COMMENT.TYPE_NAME, field = COMMENT.Seller)
  public Profile getCommentSeller(DataFetchingEnvironment dataFetchingEnvironment) {
    Comment comment = dataFetchingEnvironment.getSource();
    Map<String, CommentData> map = dataFetchingEnvironment.getLocalContext();
    return queryProfile(map.get(comment.getId()).getProfileData().getUsername());
  }

  @DgsData(parentType = DgsConstants.QUERY_TYPE, field = QUERY.Profile)
  public ProfilePayload queryProfile(
      @InputArgument("username") String username, DataFetchingEnvironment dataFetchingEnvironment) {
    Profile profile = queryProfile(dataFetchingEnvironment.getArgument("username"));
    return ProfilePayload.newBuilder().profile(profile).build();
  }

  private Profile queryProfile(String username) {
    User current = SecurityUtil.getCurrentUser().orElse(null);
    ProfileData profileData =
        profileQueryService
            .findByUsername(username, current)
            .orElseThrow(ResourceNotFoundException::new);
    return Profile.newBuilder()
        .username(profileData.getUsername())
        .bio(profileData.getBio())
        .image(profileData.getImage())
        .following(profileData.isFollowing())
        .build();
  }
}
