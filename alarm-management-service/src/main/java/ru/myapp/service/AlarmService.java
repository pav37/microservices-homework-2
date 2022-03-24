package ru.myapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.myapp.entity.Alarm;
import ru.myapp.entity.AlarmType;
import ru.myapp.repository.AlarmRepository;
import ru.myapp.repository.AlarmTypeRepository;

import java.util.List;

@Service
@Slf4j
public class AlarmService {
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private AlarmTypeRepository alarmTypeRepository;

    public List<AlarmType> getAlarmTypes() {
        return alarmTypeRepository.findAll();
    }

    public List<Alarm> getAlarms() {
        return alarmRepository.findAllByOrderByDateTimeDesc();
    }

    public void clearAlarms() {
        alarmRepository.deleteAll();
    }
}
