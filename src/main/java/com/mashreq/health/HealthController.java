package com.mashreq.health;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

  private final JdbcTemplate template;

  public HealthController(JdbcTemplate template) {
    this.template = template;
  }

  @GetMapping
  public ResponseEntity<Void> health() {
    if (isDatabaseUp()) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  private Boolean isDatabaseUp() {
    List<Object> results = template.query("SELECT 1", new SingleColumnRowMapper<>());
    int code = results.size();
    return code == 1;
  }
}
