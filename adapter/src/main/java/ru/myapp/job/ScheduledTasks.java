package ru.myapp.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.myapp.service.DeviceService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class ScheduledTasks {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	@Autowired
	private DeviceService deviceService;

	@Scheduled(fixedDelayString = "${app.value.add.interval}")
	public void addRandomValueJob() {
		log.info("addRandomValue {}", dateFormat.format(new Date()));
		deviceService.addRandomValue();
	}

	@Scheduled(fixedDelayString = "${app.value.set.interval}")
	public void setRandomValueJob() {
		log.info("setRandomValue {}", dateFormat.format(new Date()));
		deviceService.setRandomValue();
	}
}