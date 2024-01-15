package io.spring.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.item.Item;
import io.spring.core.item.ItemRepository;
import io.spring.core.user.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/items/{slug}/comments")
@AllArgsConstructor
public class CommentsApi {
  private ItemRepository itemRepository;
  private CommentRepository commentRepository;
  private CommentQueryService commentQueryService;

  @PostMapping
  public ResponseEntity<?> createComment(
      @PathVariable("slug") String slug,
      @AuthenticationPrincipal User user,
      @Valid @RequestBody NewCommentParam newCommentParam) {
    Item item = itemRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    Comment comment = new Comment(newCommentParam.getBody(), user.getId(), item.getId());
    commentRepository.save(comment);
    return ResponseEntity.status(201)
        .body(commentResponse(commentQueryService.findById(comment.getId(), user).get()));
  }

  @GetMapping
  public ResponseEntity getComments(
      @PathVariable("slug") String slug, @AuthenticationPrincipal User user) {
    Item item = itemRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    List<CommentData> comments = commentQueryService.findByItemId(item.getId(), user);
    return ResponseEntity.ok(
        new HashMap<String, Object>() {
          {
            put("comments", comments);
          }
        });
  }

  @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
  public ResponseEntity deleteComment(
      @PathVariable("slug") String slug,
      @PathVariable("id") String commentId,
      @AuthenticationPrincipal User user) {
    Item item = itemRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    return commentRepository
        .findById(item.getId(), commentId)
        .map(
            comment -> {
              commentRepository.remove(comment);
              return ResponseEntity.noContent().build();
            })
        .orElseThrow(ResourceNotFoundException::new);
  }

  private Map<String, Object> commentResponse(CommentData commentData) {
    return new HashMap<String, Object>() {
      {
        put("comment", commentData);
      }
    };
  }
}

@Getter
@NoArgsConstructor
@JsonRootName("comment")
class NewCommentParam {
  @NotBlank(message = "can't be empty")
  private String body;
}
