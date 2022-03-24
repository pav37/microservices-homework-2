package ru.myapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Setter
@Getter
public class CommandDto {
    private String deviceId;
    private String sensorId;
    private String actionType;
    private String value;
    private String userId;
    private String username;
    private String email;
    private String oldValue;
    private UUID messageId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String previousValue;

}
