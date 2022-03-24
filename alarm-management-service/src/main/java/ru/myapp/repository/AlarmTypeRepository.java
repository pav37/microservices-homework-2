package ru.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.myapp.entity.Alarm;
import ru.myapp.entity.AlarmType;

import java.util.List;

@Repository
public interface AlarmTypeRepository extends JpaRepository<AlarmType, Long> {
}