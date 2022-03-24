package ru.myapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

@Data
public class CommandDto {
    public static final String COMMAND_NAME_ADD = "add";
    public static final String COMMAND_NAME_SET = "set";

    private String deviceId;
    private String sensorId;
    private String actionType;
    private String value;
    private String oldValue;
    private String parameterName;
    private UUID messageId;
    private String userId;
    private String username;
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String previousValue;

}
