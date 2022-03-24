package ru.myapp.controller;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.myapp.entity.Device;
import ru.myapp.mapper.DeviceMapper;
import ru.myapp.model.DeviceDto;
import ru.myapp.service.DeviceService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@Timed
@Slf4j
//@Endpoint(id = "management")
public class DeviceController {

	@Autowired
	private DeviceMapper deviceMapper;
	@Autowired
	private DeviceService deviceService;

	@GetMapping("/management/devices/clear")
	@ResponseBody
	public List<DeviceDto> deleteDevices(@AuthenticationPrincipal Jwt jwt, Principal principal) {
		deviceService.deleteDevices();
		return deviceService.getDevices()
				.stream().map(d -> deviceMapper.toDto(d)).collect(Collectors.toList());
	}


	@GetMapping("/management/devices")
	@ResponseBody
	public List<DeviceDto> getDevices(@AuthenticationPrincipal Jwt jwt, Principal principal) {
		return deviceService.getDevices()
				.stream().map(d -> deviceMapper.toDto(d)).collect(Collectors.toList());
	}

	@GetMapping("/management/devices/{id}")
	@ResponseBody
	public DeviceDto getDevice(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt, Principal principal) {
		return deviceService.getDevices()
				.stream().filter(d -> d.getId().equals(id))
				.map(d -> deviceMapper.toDto(d))
				.findFirst().orElse(null);
	}

	@PostMapping("/management/create_device")
	@ResponseBody
	public Device createDeviceWithSensor(@AuthenticationPrincipal Jwt jwt, Principal principal,
										 @RequestParam UUID deviceId, @RequestParam UUID sensorId, @RequestParam String parameterName) {
		return deviceService.createDeviceWithSensor(deviceId, sensorId, parameterName);
	}

	@GetMapping("/management/create_device")
	@ResponseBody
	public Device createDeviceWithSensor2(@AuthenticationPrincipal Jwt jwt, Principal principal,
										 @RequestParam UUID deviceId, @RequestParam UUID sensorId, @RequestParam String parameterName) {
		log.info("Request: " + deviceId + " : " + sensorId + " : " + parameterName);
		return deviceService.createDeviceWithSensor(deviceId, sensorId, parameterName);
	}
}
