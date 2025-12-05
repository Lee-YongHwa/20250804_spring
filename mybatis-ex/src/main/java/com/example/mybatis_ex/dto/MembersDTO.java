package com.example.mybatis_ex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembersDTO {
  private Long mno;
  private String email;
  private String pass;
  private String name;
  private LocalDateTime regDate, modDate;

}
