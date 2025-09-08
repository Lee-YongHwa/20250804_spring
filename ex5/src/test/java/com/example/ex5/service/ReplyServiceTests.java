package com.example.ex5.service;

import com.example.ex5.dto.ReplyDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReplyServiceTests {

  @Autowired
  private ReplyService replyService;

  @Test
  void getList() {
    List<ReplyDTO> replyList = replyService.getList(50L);
    replyList.forEach(replyDTO -> System.out.println(replyDTO));
  }
}