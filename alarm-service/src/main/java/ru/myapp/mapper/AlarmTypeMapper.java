package ru.myapp.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.myapp.entity.AlarmType;
import ru.myapp.model.AlarmTypeDto;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class AlarmTypeMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(AlarmType.class, AlarmTypeDto.class);
        modelMapper.createTypeMap(AlarmTypeDto.class, AlarmType.class);
    }

    public AlarmTypeDto toDto(AlarmType entity) {
        return modelMapper.map(entity, AlarmTypeDto.class);
    }

    public AlarmType toEntity(AlarmTypeDto dto) {
        return modelMapper.map(dto, AlarmType.class);
    }
}
