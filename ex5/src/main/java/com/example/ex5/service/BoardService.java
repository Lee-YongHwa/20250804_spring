package com.example.ex5.service;

import com.example.ex5.dto.BoardDTO;
import com.example.ex5.dto.PageRequestDTO;
import com.example.ex5.dto.PageResultDTO;
import com.example.ex5.entity.Board;
import com.example.ex5.entity.Member;

public interface BoardService {
  default Board dtoToEntity(BoardDTO boardDTO) {
    return Board.builder()
        .bno(boardDTO.getBno())
        .title(boardDTO.getTitle())
        .content(boardDTO.getContent())
        .writer(Member.builder().email(boardDTO.getWriterEmail()).build())
        .build();
  }

  default BoardDTO entityToDto(Board board, Member member, Long replyCount) {
    BoardDTO boardDTO = BoardDTO.builder()
        .bno(board.getBno())
        .title(board.getTitle())
        .content(board.getContent())
        .regDate(board.getRegDate())
        .modDate(board.getModDate())
        .writerEmail(member.getEmail())
        .writerName(member.getName())
        .replyCount(replyCount.intValue())// long => int
        .build();
    return boardDTO;
  }

  Long register(BoardDTO boardDTO);

  // PageResultDTO는 view로 갈때, 핵심인 BoardDTO만 받고 나머지는 Object[]로 처리
  PageResultDTO<BoardDTO, Object[]> getList(PageRequestDTO pageRequestDTO);

  BoardDTO read(Long bno, PageRequestDTO pageRequestDTO); // Board 상세보기

  void removeWithReplies(Long bno); // Board를 지울때 댓글도 같이 지우기

  Long modify(BoardDTO boardDTO, PageRequestDTO pageRequestDTO);

  Long remove(BoardDTO boardDTO);
}
