package com.example.ex5.service;

import com.example.ex5.dto.BoardDTO;
import com.example.ex5.dto.PageRequestDTO;
import com.example.ex5.dto.PageResultDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceImplTests {
  @Autowired
  private BoardService boardService;

  @Test
  public void testRegister() {
    BoardDTO boardDTO = BoardDTO.builder()
        .title("Test...")
        .content("Content...")
        .writerEmail("user1@a.a")
        .build();
    System.out.println(">>"+boardService.register(boardDTO));
  }

  @Test
  public void testGetList() {
    PageResultDTO<BoardDTO, Object[]> result = boardService.getList(new PageRequestDTO());
    for (BoardDTO dto : result.getDtoList()) {
      System.out.println(dto);
    }
  }

  @Test
  public void testDeleteByBno() {
    boardService.removeWithReplies(1l);
  }

  @Test
  public void testModify() {
    BoardDTO boardDTO = BoardDTO.builder()
        .bno(3l)
        .title("Updated")
        .content("Updated")
        .build();
    boardService.modify(boardDTO, new PageRequestDTO());
  }
}