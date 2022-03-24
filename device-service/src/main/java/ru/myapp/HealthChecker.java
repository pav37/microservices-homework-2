package ru.myapp;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HealthChecker implements HealthIndicator {

    @Override
    public Health health() {
        return Health.status("OK").build();
    }
}