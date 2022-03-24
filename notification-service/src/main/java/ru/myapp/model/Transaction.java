package ru.myapp.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@RedisHash("transaction")
public class Transaction {
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_COMMITTED = "COMMITTED";
    public static final String STATUS_CANCELED = "CANCELED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    @Id
    private UUID id;
    private String body;
    private String status;
    private LocalDateTime dateTime;
    @Builder.Default
    @TimeToLive(unit = TimeUnit.MINUTES) Long timeout = 5L;
}
