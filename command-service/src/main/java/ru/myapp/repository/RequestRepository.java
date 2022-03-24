package ru.myapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.myapp.model.RequestDto;

import java.util.UUID;

@Repository
public interface RequestRepository extends CrudRepository<RequestDto, UUID> {
}