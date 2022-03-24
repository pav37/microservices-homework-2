package ru.myapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_groups")
public class UserGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private UUID id;
  @Column(name = "name")
  private String name;

  @ManyToMany(mappedBy = "userGroups")
  private List<User> users;
}