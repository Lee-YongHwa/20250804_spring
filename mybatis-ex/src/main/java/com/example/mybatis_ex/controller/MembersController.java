package com.example.mybatis_ex.controller;

import com.example.mybatis_ex.dto.MembersDTO;
import com.example.mybatis_ex.service.MembersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/members")
@RequiredArgsConstructor
public class MembersController {
  private final MembersService membersService;

  @GetMapping("/getMembers")
  public ResponseEntity<MembersDTO> getMembers(String email) {
    return new ResponseEntity<>(membersService.getMembers(email), HttpStatus.OK);
  }

  @PostMapping("")
  public ResponseEntity<Long> registerMembers(@RequestBody MembersDTO membersDTO) {
    return new ResponseEntity<>(membersService.registerMembers(membersDTO), HttpStatus.OK);
  }

  @PutMapping("")
  public ResponseEntity<Long> updateMembers(@RequestBody MembersDTO membersDTO) {
    return new ResponseEntity<>(membersService.updateMembers(membersDTO), HttpStatus.OK);
  }

  @DeleteMapping("/{mno}")
  public ResponseEntity<Long> deleteMembers(@PathVariable("mno") Long mno) {
    return new ResponseEntity<>(membersService.deleteMembers(mno), HttpStatus.OK);
  }

  @GetMapping("/getMembersAll")
  public ResponseEntity<List<MembersDTO>> getMembersAll() {
    return new ResponseEntity<>(membersService.getMembersAll(), HttpStatus.OK);
  }
}
