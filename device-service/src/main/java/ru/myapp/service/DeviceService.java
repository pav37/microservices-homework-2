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
import ru.myapp.entity.Sensor;
import ru.myapp.exception.DeviceCommandException;
import ru.myapp.exception.IncorrectParameterException;
import ru.myapp.exception.SensorNotFoundException;
import ru.myapp.model.CommandDto;
import ru.myapp.model.MeasureDto;
import ru.myapp.model.Transaction;
import ru.myapp.repository.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static ru.myapp.model.Transaction.*;

@Service
@Slf4j
public class DeviceService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private KafkaTemplate<Long, CommandDto> kafkaCommandDtoTemplate;
    @Autowired
    private KafkaTemplate<Long, MeasureDto> kafkaMeasureDtoTemplate;
    @Autowired
    private CommandRepository commandRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @KafkaListener(id = "device-measure-received", topics = {"adapter.measure_received"}, containerFactory = "singleFactory")
    public void consumeMeasureReceived(MeasureDto dto) {
        log.info("=> consumed {}", writeValueAsString(dto));
        deviceRepository.findById(UUID.fromString(dto.getDeviceId()))
                .flatMap(a -> a.getSensors().stream()
                        .filter(s ->
                            s.getParameter().getParameterName().equalsIgnoreCase(dto.getParameterName()))
                        .findFirst()).ifPresent(e -> {
                    e.setValue(Double.valueOf(dto.getValue()));
                    sensorRepository.save(e);
                    log.info("Saved value {} for parameter {} and sensor {}",
                            e.getValue(), e.getParameter().getParameterName(), e.getId());
                });
    }


    @KafkaListener(id = "device-value-set-error", topics = {"adapter.value_set_error"}, containerFactory = "singleFactory")
    public void consumeValueSetError(CommandDto dto) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(dto.getMessageId());
        transactionOptional.ifPresent(t -> {
            try {
                CommandDto existingCommand = readCommand(t.getBody());
                log.info("Rollback transaction {} for event {}", existingCommand, dto);

                Sensor sensor = getSensorForDevice(dto.getDeviceId(), dto.getSensorId());
                dto.setParameterName(sensor.getParameter().getParameterName());
                dto.setError("Откат транзакции " + t.getId());

                restoreValue(existingCommand.getPreviousValue(), sensor);

                updateTransactionStatus(t, t.getBody(), STATUS_CANCELED);
                sendCommandDtoEvent(dto, "dm.value_set_error");

                log.error("Transaction CANCELED for command {}", dto);
            } catch (SensorNotFoundException e) {
                log.error("Rollback transaction error", e);
            }
        });
    }

    private void restoreValue(String valueStr, Sensor sensor) {
        try {
            sensor.setValue(Double.valueOf(valueStr));
        } catch (NumberFormatException ex) {
            sensor.setValue(null);
        }
        log.info("Saving: " + sensor);
        sensorRepository.save(sensor);
    }

    private Sensor getSensorForDevice(String deviceId, String sensorIdStr) throws SensorNotFoundException {
        UUID sensorId = UUID.fromString(sensorIdStr);

        return deviceRepository.findById(UUID.fromString(deviceId))
                .flatMap(a -> a.getSensors().stream()
                        .filter(s -> s.getId().equals(sensorId))
                        .findFirst())
                .orElseThrow(() -> new SensorNotFoundException((String.format("Sensor not found for deviceId %s and sensorId %s ", deviceId, sensorId))));
    }

    @KafkaListener(id = "device-value-set", topics = {"command.value_set"}, containerFactory = "singleFactory")
    public void consumeValueAdd(CommandDto dto) {
//        Optional<CommandDto> existingCommand = commandRepository.findById(dto.getMessageId());
//        if (existingCommand.isPresent()) {
//            log.info("Command with Id {} already exists", dto.getMessageId());
//            return;
//        }
        Optional<Transaction> transactionOpt = transactionRepository.findById(dto.getMessageId());
        if (transactionOpt.isPresent()) {
            log.info("Transaction already exists {}", dto);
            return;
        }
        String dtoStr = writeValueAsString(dto);
        log.info("=> consumed {}", dtoStr);
//        commandRepository.save(dto);
        Transaction transaction = createAndSaveTransaction(STATUS_PROCESSING, dtoStr, dto.getMessageId());
        try {
            Sensor sensor = getSensorForDevice(dto.getDeviceId(), dto.getSensorId());
            dto.setParameterName(sensor.getParameter().getParameterName());
            dto.setPreviousValue(String.valueOf(sensor.getValue()));
            dtoStr = writeValueAsString(dto);

            if (dto.getActionType().equalsIgnoreCase("add")) {
                addValue(dto.getValue(), dto.getOldValue(), sensor);
            } else {
                setValue(dto.getValue(),sensor);
            }
            updateTransactionStatus(transaction, dtoStr, STATUS_COMMITTED);
            sendCommandDtoEvent(dto, "dm.value_set_approved");
        } catch (DeviceCommandException | IncorrectParameterException | SensorNotFoundException ex) {
            log.error("Exception occurred while sending command", ex);
            dto.setError(ex.getMessage());
            updateTransactionStatus(transaction, dtoStr, STATUS_FAILED);
            sendCommandDtoEvent(dto, "dm.value_set_error");
        }
    }

    private Transaction updateTransactionStatus(Transaction transaction, String body, String status) {
        transaction.setStatus(status);
        transaction.setBody(body);
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

    private void setValue(String valueStr, Sensor sensor) {
        Double value = Double.valueOf(valueStr);
        if (value > sensor.getParameter().getValueMin() && value < sensor.getParameter().getValueMax()) {
            sensor.setValue(value);
            log.info("Saving: " + sensor);
            sensorRepository.save(sensor);
        }
    }

    private void addValue(String valueStr, String oldValueStr, Sensor sensor) throws IncorrectParameterException {
        Double currentValue = sensor.getValue();
        Double value = currentValue == null ? 0.0 : currentValue + Double.parseDouble(valueStr);
        if (value > sensor.getParameter().getValueMin() && value < sensor.getParameter().getValueMax()
                && (currentValue == null || currentValue.equals(Double.parseDouble(oldValueStr))) ) {
            sensor.setValue(value);
            log.info("Saving: " + sensor);
            sensorRepository.save(sensor);
        } else {
            throw new IncorrectParameterException("Incorrect parameter value");
        }
    }

    public void sendCommandDtoEvent(CommandDto dto, String topic) {
        log.info("<= sending {}", writeValueAsString(dto));
        ListenableFuture<SendResult<Long, CommandDto>> result = kafkaCommandDtoTemplate.send(topic, dto);
        result.addCallback(new ListenableFutureCallback<SendResult<Long, CommandDto>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Send CommandDto event error", ex);
            }

            @Override
            public void onSuccess(SendResult<Long, CommandDto> result) {
                log.info("CommandDto sent result: " + result.toString());
            }
        });
    }

    private CommandDto readCommand(String str) {
        try {
            return objectMapper.readValue(str, CommandDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Reading value to JSON failed: " + str);
        }
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
