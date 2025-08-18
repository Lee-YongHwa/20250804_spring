package com.example.ex5.dto;

import com.example.ex5.entity.Member;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoardDTO {
  private Long bno;
  private String title;
  private String content;
  private String writerEmail;
  private String writerName;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
  private int replyCount; // 댓글 수
}
