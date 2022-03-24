package ru.myapp.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.myapp.entity.User;
import ru.myapp.model.UserDto;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(User.class, UserDto.class);
        modelMapper.createTypeMap(UserDto.class, User.class);
    }

    public UserDto toDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User toEntity(UserDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
