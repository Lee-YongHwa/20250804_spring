package com.example.ex3.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tbl_memo")
@Getter
@ToString
@NoArgsConstructor //매개변수가 없는 기본 생성자 생성
@AllArgsConstructor //모든 매개변수가 있는 생성자 생성
@Builder
public class Memo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long mno;

  @Column(length = 200, nullable = false)
  private String memoText;
}
