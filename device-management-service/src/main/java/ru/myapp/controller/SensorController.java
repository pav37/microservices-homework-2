package ru.myapp.controller;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.myapp.entity.Sensor;
import ru.myapp.service.DeviceService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@Timed
@Slf4j
//@Endpoint(id = "management")
public class SensorController {

	@Autowired
	private DeviceService deviceService;

	@GetMapping("/management/sensors")
	@ResponseBody
	public List<Sensor> getSensors(@AuthenticationPrincipal Jwt jwt, Principal principal) {
		return deviceService.getSensors();
	}

	@GetMapping("/management/sensors/{id}")
	@ResponseBody
	public Sensor getSensorById(@PathVariable UUID id) {
		return deviceService.getSensors().stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
	}
}
