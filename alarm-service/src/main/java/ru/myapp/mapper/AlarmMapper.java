package ru.myapp.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.myapp.entity.Alarm;
import ru.myapp.model.AlarmDto;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class AlarmMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Alarm.class, AlarmDto.class);
        modelMapper.createTypeMap(AlarmDto.class, Alarm.class);
    }

    public AlarmDto toDto(Alarm entity) {
        return modelMapper.map(entity, AlarmDto.class);
    }

    public Alarm toEntity(AlarmDto dto) {
        return modelMapper.map(dto, Alarm.class);
    }
}
