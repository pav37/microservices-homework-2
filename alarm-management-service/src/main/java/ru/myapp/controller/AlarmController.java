package ru.myapp.controller;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.myapp.entity.Alarm;
import ru.myapp.entity.AlarmType;
import ru.myapp.service.AlarmService;

import java.util.List;

@Controller
@Timed
@Slf4j
public class AlarmController {

	@Autowired
	private AlarmService alarmService;

	@ReadOperation
	@GetMapping("/alarms")
	@ResponseBody
	public List<Alarm> getAlarms() {
		return alarmService.getAlarms();
	}


	@ReadOperation
	@GetMapping("/alarms/types")
	@ResponseBody
	public List<AlarmType> getAlarmTypes() {
		return alarmService.getAlarmTypes();
	}


	@ReadOperation
	@GetMapping("/alarms/clear")
	@ResponseBody
	public List<Alarm> clearAlarms() {
		alarmService.clearAlarms();
		return alarmService.getAlarms();
	}

}
