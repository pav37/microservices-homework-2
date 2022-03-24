package ru.myapp.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.myapp.entity.Device;
import ru.myapp.model.DeviceDto;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class DeviceMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Device.class, DeviceDto.class);
        modelMapper.createTypeMap(DeviceDto.class, Device.class);
    }

    public DeviceDto toDto(Device device) {
        return modelMapper.map(device, DeviceDto.class);
    }

    public Device toEntity(DeviceDto dto) {
        return modelMapper.map(dto, Device.class);
    }
}
