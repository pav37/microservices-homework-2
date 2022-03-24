package ru.myapp.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@ToString
@RedisHash("Notification")
public class Notification implements Serializable {
  @Id
  private Long id;
  private String username;
  private String email;
  private String title;
  private String body;
  private String description;
  private LocalDateTime dateTime;
  private String eventType;
  private String message;

  @Builder.Default
  @TimeToLive(unit = TimeUnit.MINUTES) Long timeout = 5L;
}