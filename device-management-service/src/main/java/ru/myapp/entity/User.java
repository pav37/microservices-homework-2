package ru.myapp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "username")
  private String username;
  @Column(name = "email")
  private String email;


  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "users_user_groups",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "user_group_id"))
  private List<UserGroup> userGroups;
}