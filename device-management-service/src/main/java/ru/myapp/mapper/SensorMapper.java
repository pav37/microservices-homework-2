package ru.myapp.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.myapp.entity.Sensor;
import ru.myapp.model.SensorDto;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class SensorMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Sensor.class, SensorDto.class);
        modelMapper.createTypeMap(SensorDto.class, Sensor.class);
    }

    public SensorDto toDto(Sensor sensor) {
        return modelMapper.map(sensor, SensorDto.class);
    }

    public Sensor toEntity(SensorDto dto) {
        return modelMapper.map(dto, Sensor.class);
    }
}
