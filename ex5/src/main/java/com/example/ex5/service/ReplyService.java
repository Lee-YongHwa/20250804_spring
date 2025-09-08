package com.example.ex5.service;

import com.example.ex5.dto.ReplyDTO;
import com.example.ex5.entity.Board;
import com.example.ex5.entity.Reply;

import java.util.List;

public interface ReplyService {
  Long register(ReplyDTO replyDTO);

  List<ReplyDTO> getList(Long bno);

  void modify(ReplyDTO replyDTO);

  void remove(Long rno);

  default Reply dtoToEntity(ReplyDTO replyDTO) {
    Reply reply = Reply.builder()
        .rno(replyDTO.getRno())
        .text(replyDTO.getText())
        .commenter(replyDTO.getCommenter())
        .board(Board.builder().bno(replyDTO.getBno()).build())
        .build();
    return reply;
  }

  default ReplyDTO entityToDto(Reply reply) {
    return ReplyDTO.builder()
        .rno(reply.getRno())
        .text(reply.getText())
        .commenter(reply.getCommenter())
        .bno(reply.getBoard().getBno())
        .regDate(reply.getRegDate())
        .modDate(reply.getModDate())
        .build();
  }
}
