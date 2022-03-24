package ru.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.myapp.entity.DeviceGroup;

import java.util.UUID;

@Repository
public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, UUID> {
}