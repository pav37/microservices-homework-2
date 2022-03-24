package ru.myapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "parameters")
public class Parameter {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;
  @Column(name = "parameter_name")
  private String parameterName;
  @Column(name = "value_min")
  private Double valueMin;
  @Column(name = "value_max")
  private Double valueMax;

}