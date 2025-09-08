package com.example.ex6.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Table(name = "m_member") // 실제 테이블명을 다른 이름으로 생성
public class Member extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long mid;
  private String email;
  private String pw;
  private String nickname;
}
