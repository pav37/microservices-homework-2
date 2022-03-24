package ru.myapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.myapp.entity.Device;
import ru.myapp.entity.Sensor;
import ru.myapp.repository.*;

import java.util.List;
import java.util.Random;
import java.util.UUID;

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
    private DeviceGroupRepository deviceGroupRepository;
    @Autowired
    private ParameterRepository parameterRepository;

    public void deleteDevices() {
        deviceRepository.deleteAll();
    }

    public List<Device> getDevices() {
        return deviceRepository.findAll();
    }

    public List<Sensor> getSensors() {
        return sensorRepository.findAll();
    }

    public Device createDeviceWithSensor(UUID deviceId, UUID sensorId, String parameterName) {
        Device device = new Device();
        device.setId(deviceId);
        device.setName("Test Device " + new Random().nextInt(100));
        device.setIsTest(true);
        device.setDeviceGroup(deviceGroupRepository.findAll().stream()
                .filter(a -> a.getName().equalsIgnoreCase("test"))
                .findFirst().orElse(null));
        log.info(device.toString());
        deviceRepository.save(device);
        Sensor sensor = new Sensor();
        sensor.setDevice(device);
        sensor.setId(sensorId);
        sensor.setParameter(parameterRepository.findAll().stream()
                .filter(p -> p.getName().equalsIgnoreCase(parameterName)).findFirst()
                .orElse(null));
        log.info(sensor.toString());
        sensorRepository.save(sensor);
        return deviceRepository.getById(deviceId);
    }
}
