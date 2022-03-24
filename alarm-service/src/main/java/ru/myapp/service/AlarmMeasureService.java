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
import ru.myapp.entity.Alarm;
import ru.myapp.entity.AlarmType;
import ru.myapp.mapper.AlarmMapper;
import ru.myapp.model.AlarmDto;
import ru.myapp.model.MeasureDto;
import ru.myapp.repository.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class AlarmMeasureService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaTemplate<Long, AlarmDto> kafkaAlarmDtoTemplate;
    @Autowired
    private AlarmTypeRepository alarmTypeRepository;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private AlarmMapper alarmMapper;

    @KafkaListener(id = "device-measure-received", topics = {"adapter.measure_received"}, containerFactory = "singleFactory")
    public void consumeMeasureReceived(MeasureDto dto) {
        log.info("=> consumed {}", writeValueAsString(dto));
        double value = Double.parseDouble(dto.getValue());
        alarmTypeRepository.findAll().stream()
                .filter(at -> at.getParameterName().equalsIgnoreCase(dto.getParameterName()))
                .filter(at -> (at.getMinValue() == null || value >= at.getMinValue()) &&
                                        (at.getMaxValue() == null || value < at.getMaxValue()))
                .forEach(at ->  {
                    if (!alarmExists(at, dto.getDeviceId())) {
                        log.info("Create alarm {} {}", dto, at);
                        Alarm a = Alarm.builder()
                                .id(UUID.randomUUID())
                                .alarmType(at)
                                .dateTime(LocalDateTime.now())
                                .deviceId(UUID.fromString(dto.getDeviceId()))
                                .parameterName(dto.getParameterName())
                                .status(Alarm.STATUS_OPENED)
                                .value(value).build();
                        log.info("Save alarm {}", a);
                        alarmRepository.save(a);
                        log.info("Send notification for alarm {}", a);
                        sendAlarmDtoEvent(alarmMapper.toDto(a), "alarm.measure_alarm");
                    }
        });
    }

    private boolean alarmExists(AlarmType alarmType, String deviceId) {
        return alarmRepository.findAll().stream()
                .anyMatch(a -> a.getAlarmType().getId().equals(alarmType.getId())
                        && a.getDeviceId().equals(UUID.fromString(deviceId))
                        && a.getStatus().equalsIgnoreCase(Alarm.STATUS_OPENED));
    }

    public void sendAlarmDtoEvent(AlarmDto dto, String topic) {
        log.info("<= sending {}", writeValueAsString(dto));
        ListenableFuture<SendResult<Long, AlarmDto>> result = kafkaAlarmDtoTemplate.send(topic, dto);
        result.addCallback(new ListenableFutureCallback<SendResult<Long, AlarmDto>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Send MeasureDto event error", ex);
            }

            @Override
            public void onSuccess(SendResult<Long, AlarmDto> result) {
                log.info("MeasureDto sent result: " + result.toString());
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
