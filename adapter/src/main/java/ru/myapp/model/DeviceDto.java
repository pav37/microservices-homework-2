package ru.myapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Data
@RedisHash("device")
public class DeviceDto {
    @Id
    private String deviceId;
    private String parameterName;
    private String value;
    private boolean changeDisabled;
    @JsonIgnore
    @TimeToLive(unit = TimeUnit.MINUTES) Long timeout = 5L;

    public static DeviceDto fromCommand(CommandDto commandDto) {
        DeviceDto dto = new DeviceDto();
        dto.setDeviceId(commandDto.getDeviceId());
        dto.setParameterName(commandDto.getParameterName());
        dto.setValue(commandDto.getValue());
        dto.setChangeDisabled(false);
        return dto;
    }
}
