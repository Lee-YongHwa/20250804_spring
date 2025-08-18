package com.example.ex5.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "board")
public class Reply extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long rno;
  private String text;
  private String commenter;
  @ManyToOne(fetch = FetchType.LAZY)
  private Board board;
  public void changeText(String text) {this.text=text;}
}
