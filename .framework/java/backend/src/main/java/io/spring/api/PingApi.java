package io.spring.api;

import io.spring.infrastructure.service.SendEventService;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/ping")
@AllArgsConstructor
public class PingApi {
  @GetMapping
  public ResponseEntity ping() {
    SendEventService sendEventService = new SendEventService();
    String response = sendEventService.sendEvent("ping", new HashMap<>());
    return ResponseEntity.ok(response);
  }
}
