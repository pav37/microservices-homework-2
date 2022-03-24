package ru.myapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.myapp.entity.User;
import ru.myapp.exception.UserNotFoundException;
import ru.myapp.mapper.UserMapper;
import ru.myapp.model.UserDto;
import ru.myapp.model.UserRegistrationDto;
import ru.myapp.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private KafkaTemplate<Long, UserDto> kafkaUserDtoTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(u -> userMapper.toDto(u))
                .collect(Collectors.toList());
    }

    public UserDto registerUser(UserRegistrationDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setEnabled(true);
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        UserDto result = userMapper.toDto(userRepository.save(user));
        send(result);
        log.debug("Return result user");
        return result;
    }

    private String writeValueAsString(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Writing value to JSON failed: " + dto.toString());
        }
    }

    public void send(UserDto dto) {
        log.info("<= sending {}", writeValueAsString(dto));
        ListenableFuture<SendResult<Long, UserDto>> result = kafkaUserDtoTemplate.send("auth.user_created", dto);
        result.addCallback(new ListenableFutureCallback<SendResult<Long, UserDto>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Exception occurred while sending event user_created", ex);
            }

            @Override
            public void onSuccess(SendResult<Long, UserDto> result) {
                log.info("result: " + result.toString());
            }
        });
    }

    public UserDto updateUser(User updatedUser, Long id) {
        return userRepository.findById(id)
            .map(user -> {
                validatePermissionForUser(user);
                user.setFirstName(updatedUser.getFirstName());
                user.setLastName(updatedUser.getLastName());
                return userMapper.toDto(userRepository.save(user));
            })
            .orElseThrow(() -> new UserNotFoundException("User not found id: " + id));
    }

    public UserDto getUser(Long id) {
        return userRepository.findById(id)
            .map(user -> {
                validatePermissionForUser(user);
                return userMapper.toDto(user);
            }).orElseThrow(() -> new UserNotFoundException("User not found id: " + id));
    }

    private void validatePermissionForUser(User user) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (!currentUser.getName().equalsIgnoreCase(user.getUsername())) {
            throw new AccessDeniedException("Access denied for user " + user.getUsername());
        }
    }

    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            validatePermissionForUser(user.get());
            userRepository.delete(user.get());
        }
    }
}
