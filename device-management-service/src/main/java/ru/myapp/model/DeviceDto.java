package ru.myapp.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeviceDto {
    private UUID id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    private List<SensorDto> sensors;
}