package ru.myapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
@RedisHash("command")
public class CommandDto {
    public static final String COMMAND_NAME_ADD = "add";
    public static final String COMMAND_NAME_SET = "set";

    private String deviceId;
    private String sensorId;
    private String actionType;
    private String value;
    private String oldValue;
    private String parameterName;
    @Id
    private UUID messageId;
    private String userId;
    private String username;
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String previousValue;
    @JsonIgnore
    @TimeToLive(unit = TimeUnit.MINUTES) Long timeout = 5L;

}
