package ru.myapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.myapp.model.CommandDto;

import java.util.UUID;

@Repository
public interface CommandRepository extends CrudRepository<CommandDto, UUID> {
}