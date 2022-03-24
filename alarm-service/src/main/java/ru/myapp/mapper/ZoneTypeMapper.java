package ru.myapp.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.myapp.entity.ZoneType;
import ru.myapp.model.ZoneTypeDto;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class ZoneTypeMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(ZoneType.class, ZoneTypeDto.class);
        modelMapper.createTypeMap(ZoneTypeDto.class, ZoneType.class);
    }

    public ZoneTypeDto toDto(ZoneType entity) {
        return modelMapper.map(entity, ZoneTypeDto.class);
    }

    public ZoneType toEntity(ZoneTypeDto dto) {
        return modelMapper.map(dto, ZoneType.class);
    }
}
