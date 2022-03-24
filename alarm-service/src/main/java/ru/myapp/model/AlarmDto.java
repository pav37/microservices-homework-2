package ru.myapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDto {
    private UUID id;
    private UUID deviceId;
    private String parameterName;
    private Double value;
    private AlarmTypeDto alarmType;
    private LocalDateTime dateTime;
}
