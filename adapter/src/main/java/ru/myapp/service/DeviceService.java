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
import ru.myapp.exception.DeviceCommandException;
import ru.myapp.model.CommandDto;
import ru.myapp.model.DeviceDto;
import ru.myapp.model.MeasureDto;
import ru.myapp.model.Transaction;
import ru.myapp.repository.DeviceRepository;
import ru.myapp.repository.TransactionRepository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static ru.myapp.model.CommandDto.COMMAND_NAME_ADD;
import static ru.myapp.model.Transaction.*;

@Service
@Slf4j
public class DeviceService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private KafkaTemplate<Long, CommandDto> kafkaCommandDtoTemplate;
    @Autowired
    private KafkaTemplate<Long, MeasureDto> kafkaMeasureDtoTemplate;

    @KafkaListener(id = "adapter-in", topics = {"dm.value_set_approved"}, containerFactory = "singleFactory")
    public void consumeValueSet(CommandDto dto) {
        String dtoStr = writeValueAsString(dto);
        log.info("=> consumed {}", dtoStr);

        Optional<Transaction> transactionOpt = transactionRepository.findById(dto.getMessageId());
        if (transactionOpt.isPresent()) {
            log.info("Transaction already exists {}", dto);
            return;
        }
        Transaction transaction = createAndSaveTransaction(STATUS_PROCESSING, dtoStr, dto.getMessageId());

        Optional<DeviceDto> existingDevice = deviceRepository.findById(UUID.fromString(dto.getDeviceId()));
        if (!existingDevice.isPresent()) {
            log.info("Creating Device from command {}", dto);
            existingDevice = Optional.of(deviceRepository.save(DeviceDto.fromCommand(dto)));
        }

        try {
            String value = dto.getValue();
            if (Double.parseDouble(value) > 70) {
                throw new DeviceCommandException(transaction.getId());
            }
            if (Double.parseDouble(value) == 0) {
                existingDevice.get().setChangeDisabled(false);
                value = String.format("%.1f", getDoubleInRange(1.0, 50.0));
            } else {
                existingDevice.get().setChangeDisabled(true);
            }
            log.info("Set changeDisabled {} for Device {} and value {}", existingDevice.get().isChangeDisabled(), existingDevice, value);
            if (dto.getActionType().equalsIgnoreCase(COMMAND_NAME_ADD)) {
                existingDevice.get().setValue(String.valueOf(Double.parseDouble(existingDevice.get().getValue()) + Double.parseDouble(value)));
            } else {
                existingDevice.get().setValue(value);
            }
            deviceRepository.save(existingDevice.get());
            log.info("Send Command {}", dto);
            updateTransactionStatus(transaction, STATUS_COMPLETED);
            sendCommandDtoEvent(dto, "adapter.value_set_sent");
        } catch (DeviceCommandException ex) {
            log.error("Exception occurred while sending command", ex);
            dto.setError("Некорректное значение");
            updateTransactionStatus(transaction, STATUS_FAILED);
            sendCommandDtoEvent(dto, "adapter.value_set_error");
        }
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

    private Double getDoubleInRange(Double min, Double max) {
        return min + new Random().nextDouble() * (max - min);
    }

    public void addRandomValue() {
        deviceRepository.findAll().forEach(d -> {
            if (!d.isChangeDisabled()) {
                String value = String.format("%.1f", getDoubleInRange(1.0, 3.0));
                String oldValue = d.getValue();
                log.info("Debug: {} {}", Double.parseDouble(oldValue), Double.parseDouble(value));
                d.setValue(String.format("%.1f", BigDecimal.valueOf(Double.parseDouble(oldValue)).add(BigDecimal.valueOf(Double.parseDouble(value)))));
                log.info("Add value {}: {} to {} result: {}", d.getDeviceId(), value, oldValue, d.getValue());
                deviceRepository.save(d);
            }
            MeasureDto dto = MeasureDto.builder()
                    .deviceId(d.getDeviceId())
                    .parameterName(d.getParameterName())
                    .value(d.getValue())
                    .created(LocalDateTime.now())
                    .build();
            sendMeasureDtoEvent(dto, "adapter.measure_received");
        });
    }

    public void setRandomValue() {
        deviceRepository.findAll().forEach(d -> {
            if (!d.isChangeDisabled()) {
                String value = String.format("%.1f", getDoubleInRange(0.1, 50.0));
                String oldValue = d.getValue();
                d.setValue(value);
                log.info("Set value {}: {} to {} result: {}", d.getDeviceId(), value, oldValue, d.getValue());
                deviceRepository.save(d);
            }
            MeasureDto dto = MeasureDto.builder()
                    .deviceId(d.getDeviceId())
                    .parameterName(d.getParameterName())
                    .value(d.getValue())
                    .created(LocalDateTime.now())
                    .build();
            sendMeasureDtoEvent(dto, "adapter.measure_received");
        });
    }

    public void sendMeasureDtoEvent(MeasureDto dto, String topic) {
        log.info("<= sending MeasureDto {}", writeValueAsString(dto));
        ListenableFuture<SendResult<Long, MeasureDto>> result = kafkaMeasureDtoTemplate.send(topic, dto);
        result.addCallback(new ListenableFutureCallback<SendResult<Long, MeasureDto>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Send MeasureDto event error", ex);
            }

            @Override
            public void onSuccess(SendResult<Long, MeasureDto> result) {
                log.info("MeasureDto result: " + result.toString());
            }
        });
    }

    public void sendCommandDtoEvent(CommandDto dto, String topic) {
        log.info("<= sending {} {}", topic, writeValueAsString(dto));
        ListenableFuture<SendResult<Long, CommandDto>> result = kafkaCommandDtoTemplate.send(topic, dto);
        result.addCallback(new ListenableFutureCallback<SendResult<Long, CommandDto>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Send CommandDto event error", ex);
            }

            @Override
            public void onSuccess(SendResult<Long, CommandDto> result) {
                log.info("CommandDto result: " + result.toString());
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
