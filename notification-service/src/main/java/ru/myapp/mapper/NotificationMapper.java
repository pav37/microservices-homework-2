package ru.myapp.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.myapp.entity.Notification;
import ru.myapp.model.NotificationDto;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class NotificationMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Notification.class, NotificationDto.class);
        modelMapper.createTypeMap(NotificationDto.class, Notification.class);
    }

    public NotificationDto toDto(Notification entity) {
        return modelMapper.map(entity, NotificationDto.class);
    }

    public Notification toEntity(NotificationDto dto) {
        return modelMapper.map(dto, Notification.class);
    }
}
