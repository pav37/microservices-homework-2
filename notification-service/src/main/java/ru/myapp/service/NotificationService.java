package ru.myapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.myapp.entity.Notification;
import ru.myapp.mapper.NotificationMapper;
import ru.myapp.model.*;
import ru.myapp.repository.NotificationRepository;
import ru.myapp.repository.TransactionRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private KafkaTemplate<Long, NotificationDto> kafkaNotificationDtoTemplate;
    @Autowired
    private PushServiceClient pushServiceClient;
    @Autowired
    private TransactionRepository transactionRepository;

    @KafkaListener(id = "notification-auth", topics = {"auth.user_created"}, containerFactory = "singleFactory")
    public void consume(Object dto) {
        ConsumerRecord record = (ConsumerRecord)dto;
        try {
            UserDto user = objectMapper.readValue(record.value().toString(), UserDto.class);
            log.info("=> consumed {}", writeValueAsString(user));
            Notification n = createNotification(record, user.getUsername(), user.getEmail());
            notificationRepository.save(n);
            CompletableFuture.supplyAsync(() -> {
                pushServiceClient.send(n);
                return null;
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private Notification createNotification(ConsumerRecord record, String username, String email) {
        Notification n = new Notification();
        n.setBody(record.value().toString());
        String title = getTitle(record.topic());
        n.setTitle(title);
        n.setDescription(title);
        n.setEventType(record.topic());
        n.setDateTime( LocalDateTime.ofInstant(Instant.ofEpochMilli(record.timestamp()),
                TimeZone.getDefault().toZoneId()));
        n.setEmail(email);
        n.setUsername(username);
        return n;
    }

    @KafkaListener(id = "notification-value-set", containerFactory = "singleFactory",
            topics = {"adapter.value_set_sent", "dm.value_set_error", "adapter.value_set_error"})
    public void consumeMessage(Object dto) {
        ConsumerRecord record = (ConsumerRecord)dto;
        try {
            CommandDto command = objectMapper.readValue(record.value().toString(), CommandDto.class);
            log.info("=> consumed {}", writeValueAsString(command));
            Notification n = createNotification(record, command.getUsername(), command.getEmail());
            if (command.getError() != null) {
                n.setDescription(String.format("%s %s", n.getTitle(), command.getError()));
            }
            notificationRepository.save(n);
            CompletableFuture.supplyAsync(() -> {
                pushServiceClient.send(n);
                return null;
            });
            sendNotificationDtoEvent(notificationMapper.toDto(n), String.format("notification.%s", record.topic().split("\\.")[1]));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @KafkaListener(id = "notification-measure", containerFactory = "singleFactory", topics = {"adapter.measure_received"})
    public void consumeMeasure(Object dto) {
        ConsumerRecord record = (ConsumerRecord)dto;
        try {
            CommandDto command = objectMapper.readValue(record.value().toString(), CommandDto.class);
            log.info("=> consumed {}", writeValueAsString(command));
            Notification n = createNotification(record, command.getUsername(), command.getEmail());
            if (command.getError() != null) {
                n.setDescription(String.format("%s %s", n.getTitle(), command.getError()));
            }
            notificationRepository.save(n);
            CompletableFuture.supplyAsync(() -> {
                pushServiceClient.send(n);
                return null;
            });
            sendNotificationDtoEvent(notificationMapper.toDto(n), String.format("notification.%s", record.topic().split("\\.")[1]));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @KafkaListener(id = "notification-alarms", containerFactory = "singleFactory", topics = {"alarm.measure_alarm"})
    public void consumeAlarms(Object dto) {
        ConsumerRecord record = (ConsumerRecord)dto;
        log.info("=> consumed {}", writeValueAsString(dto));
        Notification n = createNotification(record, null,null);
        notificationRepository.save(n);
        CompletableFuture.supplyAsync(() -> {
            pushServiceClient.send(n);
            return null;
        });
        sendNotificationDtoEvent(notificationMapper.toDto(n), String.format("notification.%s", record.topic().split("\\.")[1]));
    }

    private Transaction updateTransactionStatus(Transaction transaction, String status) {
        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }

    private Transaction createAndSaveTransaction(String status, String body, UUID id) {
        return transactionRepository.save(Transaction.builder()
                .id(id)
                .body(body)
                .status(status)
                .dateTime(LocalDateTime.now())
                .build());
    }

    private String getTitle(String topic) {
        if (Objects.equals("dm.value_set_approved", topic)) {
            return "Команда обработана/подтверждена";
        } else if (Objects.equals("command.value_set", topic)) {
            return "Команда обработана";
        } else if (Objects.equals("adapter.value_set_error", topic)) {
            return "Ошибка при выполнении команды";
        } else if (Objects.equals("adapter.value_set_sent", topic)) {
            return "Команда отправлена";
        } else if (Objects.equals("dm.value_set_error", topic)) {
            return "Ошибка при обработке команды";
        } else if (Objects.equals("auth.user_created", topic)) {
            return "Создан аккаунт";
        } else if (Objects.equals("alarm.measure_alarm", topic)) {
            return "Тревога";
        } else if (Objects.equals("adapter.measure_received", topic)) {
            return "Получено измерение";
        }
        return "";
    }

    public void deleteNotifications() {
        notificationRepository.deleteAll();
    }

    public Iterable<Notification> getNotifications() {
        return notificationRepository.findAll();
    }

    public void sendNotificationDtoEvent(NotificationDto dto, String topic) {
        log.info("<= sending {}", writeValueAsString(dto));
        ListenableFuture<SendResult<Long, NotificationDto>> result = kafkaNotificationDtoTemplate.send(topic, dto);
        result.addCallback(new ListenableFutureCallback<SendResult<Long, NotificationDto>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Send NotificationDto event error", ex);
            }

            @Override
            public void onSuccess(SendResult<Long, NotificationDto> result) {
                log.info("NotificationDto sent result: " + result.toString());
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
