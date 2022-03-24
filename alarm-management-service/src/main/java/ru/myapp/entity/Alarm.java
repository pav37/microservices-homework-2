package ru.myapp.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "alarms")
public class Alarm {
  public final static String STATUS_OPENED = "OPENED";
  public final static String STATUS_CLOSED = "CLOSED";

  @Id
  private UUID id;
  @Column(name = "value")
  private Double value;
  @Column(name = "datetime")
  private LocalDateTime dateTime;
  @Column(name = "device_id")
  private UUID deviceId;
  @Column(name = "parameter_name")
  private String parameterName;
  @ManyToOne
  private AlarmType alarmType;
  @Column(name = "status")
  private String status;
}