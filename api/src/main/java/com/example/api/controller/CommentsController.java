package com.example.api.controller;

import com.example.api.dto.CommentsDTO;
import com.example.api.service.CommentsService;
import com.example.api.service.MembersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentsController {
  private final CommentsService commentsService;
  private final MembersService membersService;

  @GetMapping(value = "/all/{jno}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<CommentsDTO>> getList(@PathVariable("jno") Long jno) {
    List<CommentsDTO> result = commentsService.getList(jno);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/{jno}")
  public ResponseEntity<Long> register(@RequestBody CommentsDTO commentsDTO){
    log.info(">>"+commentsDTO);
    commentsDTO.setMid(membersService.getMembersByEmail(commentsDTO.getEmail()).getMid());
    Long cno = commentsService.register(commentsDTO);
    return new ResponseEntity<>(cno, HttpStatus.OK);
  }

  @PutMapping("/{jno}/{cno}")
  public ResponseEntity<Long> modify(@RequestBody CommentsDTO commentsDTO){
    commentsService.modify(commentsDTO);
    return new ResponseEntity<>(commentsDTO.getCno(), HttpStatus.OK);
  }

  @DeleteMapping("/{jno}/{cno}")
  public ResponseEntity<Long> delete(@PathVariable("cno") Long cno) {
    commentsService.remove(cno);
    return new ResponseEntity<>(cno, HttpStatus.OK);
  }
}