package ru.myapp.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.myapp.entity.User;
import ru.myapp.model.CustomUser;
import ru.myapp.model.UserDto;
import ru.myapp.model.UserRegistrationDto;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(User.class, UserDto.class);
        modelMapper.createTypeMap(User.class, CustomUser.class);
        modelMapper.createTypeMap(CustomUser.class, User.class);
        modelMapper.createTypeMap(UserDto.class, User.class);
        modelMapper.createTypeMap(UserRegistrationDto.class, User.class);
    }

    public UserDto toDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public CustomUser toCustomUser(User user) {
        return modelMapper.map(user, CustomUser.class);
    }

    public User toEntity(UserDto dto) {
        return modelMapper.map(dto, User.class);
    }
    public User toEntity(CustomUser dto) {
        return modelMapper.map(dto, User.class);
    }

    public User toEntity(UserRegistrationDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
