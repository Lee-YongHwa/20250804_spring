package com.example.ex5.controller;

import com.example.ex5.dto.ReplyDTO;
import com.example.ex5.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
@RestController
public class ReplyController {
  private final ReplyService replyService;

  // Controller:동기적 요청에서 view 포함한 다양한 데이터 포맷을 처리
  // RestController:비동기적 요청에서 view 제외하고 JSON/XML 형식의 데이터를 받기 위한 요청

  @GetMapping(value = "/{bno}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ReplyDTO>> getListByBoard(@PathVariable("bno") Long bno) {
    return new ResponseEntity<>(replyService.getList(bno), HttpStatus.OK);
  }

  @PostMapping(value = {"", "/"})
  public ResponseEntity<String> register(@RequestBody ReplyDTO replyDTO) {
    log.info(">>>"+replyDTO);
    String result = replyService.register(replyDTO) + "번 댓글이 등록되었습니다.";
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PutMapping(value = {"", "/"})
  public ResponseEntity<String> modify(@RequestBody ReplyDTO replyDTO) {
    log.info(">>>"+replyDTO);
    replyService.modify(replyDTO);
    return new ResponseEntity<>(replyDTO.getRno()+"번 댓글 수정.", HttpStatus.OK);
  }

  @DeleteMapping(value = {"", "/"})
  public ResponseEntity<String> delete(@RequestBody ReplyDTO replyDTO) {
    log.info(">>>"+replyDTO);
    replyService.remove(replyDTO.getRno());
    return new ResponseEntity<>(replyDTO.getRno()+"번 댓글 삭제.", HttpStatus.OK);
  }

}
