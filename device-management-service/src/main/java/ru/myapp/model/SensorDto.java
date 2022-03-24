package ru.myapp.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SensorDto {
    private UUID id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String parameterName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String value;
}