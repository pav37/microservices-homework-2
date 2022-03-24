package ru.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.myapp.entity.AlarmType;
import ru.myapp.entity.ZoneType;

@Repository
public interface ZoneTypeRepository extends JpaRepository<ZoneType, Long> {
}