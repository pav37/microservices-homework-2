package ru.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.myapp.entity.Parameter;

import java.util.UUID;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, UUID> {
}