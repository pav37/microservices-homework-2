package ru.myapp.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
@Setter
@Getter
@Builder
@RedisHash("request")
public class RequestDto {
    private String body;
    @Id
    private UUID requestId;
    @TimeToLive(unit = TimeUnit.MINUTES) Long timeout = 5L;
}
