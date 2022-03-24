package ru.myapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.myapp.entity.User;
import ru.myapp.entity.UserGroup;
import ru.myapp.mapper.UserMapper;
import ru.myapp.model.UserDto;
import ru.myapp.repository.UserGroupRepository;
import ru.myapp.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private static final String GROUP_NAME_ALL = "all";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private KafkaTemplate<Long, UserDto> kafkaUserDtoTemplate;

    @KafkaListener(id = "device-management-service0", topics = {"auth.user_created"}, containerFactory = "singleFactory")
    public void consume(UserDto dto) {
        log.info("=> consumed {}", writeValueAsString(dto));
        User user = userMapper.toEntity(dto);
        user = userRepository.save(user);
        sendUserCreatedEvent(userMapper.toDto(user));
        addUserToGroups(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    private void addUserToGroups(final User user) {
        Optional<UserGroup> userGroup = userGroupRepository.findByName(GROUP_NAME_ALL);
        userGroup.ifPresent(group -> user.setUserGroups(Collections.singletonList(group)));
    }

    public void sendUserCreatedEvent(UserDto dto) {
        log.info("<= sending {}", writeValueAsString(dto));
        ListenableFuture<SendResult<Long, UserDto>> result = kafkaUserDtoTemplate.send("dm.user_created", dto);
        result.addCallback(new ListenableFutureCallback<SendResult<Long, UserDto>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Send user created event error", ex);
            }

            @Override
            public void onSuccess(SendResult<Long, UserDto> result) {
                log.info("result: " + result.toString());
            }
        });
    }

    private String writeValueAsString(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Writing value to JSON failed: " + dto.toString());
        }
    }
}
