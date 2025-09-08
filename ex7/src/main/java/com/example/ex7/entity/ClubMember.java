package com.example.ex7.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ClubMember extends BasicEntity {
  @Id
  private String email;

  private String password;
  private String name;
  private boolean fromSocial;

  public void addMemberRole(ClubMemberRole clubMemberRole) {
    roleSet.add(clubMemberRole);
  }

  // 관련된 ClubMemberRole을 즉각적으로 조인.
  @ElementCollection(fetch = FetchType.LAZY)
  // collection 이기 때문에 default 생성을 할 수 있다.
  @Builder.Default
  private Set<ClubMemberRole> roleSet = new HashSet<>();

  public void setPassword(String password) {this.password = password;}

  public void setName(String name) {this.name = name;}
}
