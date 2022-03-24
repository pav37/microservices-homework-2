package ru.myapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.myapp.model.CommandDto;
import ru.myapp.model.RequestDto;
import ru.myapp.repository.RequestRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CommandService {

    @Autowired
    private KafkaTemplate<Long, CommandDto> kafkaCommandDtoTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RequestRepository requestRepository;

    private String writeValueAsString(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Writing value to JSON failed: " + dto.toString());
        }
    }

    public void sendCommand(CommandDto dto, UUID requestId) {
        Optional<RequestDto> existingRequest = requestRepository.findById(requestId);
        if (existingRequest.isPresent()) {
            log.info("Request with Id {} already exists", requestId);
            return;
        }
        dto.setMessageId(UUID.randomUUID());
        String body = writeValueAsString(dto);
        log.info("<= sending {}", body);
        ListenableFuture<SendResult<Long, CommandDto>> result = kafkaCommandDtoTemplate.send("command.value_set", dto);
        result.addCallback(new ListenableFutureCallback<SendResult<Long, CommandDto>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Exception occurred while sending event value_set", ex);
            }

            @Override
            public void onSuccess(SendResult<Long, CommandDto> result) {
                requestRepository.save(RequestDto.builder().body(body).requestId(requestId).build());
                log.info("result: " + result.toString());
            }
        });
    }
}
